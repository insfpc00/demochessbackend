package es.fporto.demo.minichess.user.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.fporto.demo.minichess.player.WebUser;
import es.fporto.demo.minichess.repository.AvatarRepository;
import es.fporto.demo.minichess.repository.EloScoreRepository;
import es.fporto.demo.minichess.repository.RoleRepository;
import es.fporto.demo.minichess.repository.UserRepository;
import es.fporto.demo.minichess.user.model.Avatar;
import es.fporto.demo.minichess.user.model.EloScore;
import es.fporto.demo.minichess.user.model.Role;
import es.fporto.demo.minichess.user.model.User;
import es.fporto.demo.minichess.user.model.User.FideTitles;
import es.fporto.demo.minichess.user.model.User.Themes;
import es.fporto.demo.minichess.user.model.UserDto;


@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AvatarRepository avatarRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private BCryptPasswordEncoder bcryptEncoder;
	
	@Autowired 
	private EloScoreRepository eloScoreRepository; 

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User	user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				getAuthority(user));
	}

	private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
		});
		return authorities;
	}
	
	public void changePassword(String username, String oldPassword, String newPassword) throws BadCredentialsException{
		User user=findOne(username);
		
		if (!bcryptEncoder.matches(oldPassword, user.getPassword())){
			throw new BadCredentialsException("Invalid old password");
		}
		user.setPassword(bcryptEncoder.encode(newPassword));
		userRepository.save(user);
	}
	
	public UserDto update(String username, Optional<FideTitles> title, Optional<Themes> theme, Optional<String> country,Optional<Boolean> soundEnabled) {
		
		User user=findOne(username);
		
		if (title.isPresent()) {
			user.setFideTitle(title.get());
		}
		if (theme.isPresent()) {
			user.setTheme(theme.get());
		}
		if (country.isPresent()) {
			user.setCountry(country.get());
		}
		
		if (soundEnabled.isPresent()) {
			user.setSoundEnabled(soundEnabled.get());
		}
		userRepository.save(user);
		
		return new UserDto(user);
	}
	
	public List<User> findAll() {
		List<User> list = new ArrayList<>();
		userRepository.findAll().iterator().forEachRemaining(list::add);
		return list;
	}

	public User findOne(String username) {
		return  userRepository.findById(username).get();
	}

	public User save(UserDto user,String... roles) {
		User newUser = new WebUser();
	    newUser.setUsername(user.getUsername());
	    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
	    newUser.setFirstName(user.getFirstName());
	    newUser.setLastName(user.getLastName());
	    newUser.setDateOfBirth(user.getDateOfBirth());
	    newUser.setFideTitle(user.getFideTitle());
	    newUser.setCountry(user.getCountry());
	    newUser.setCreationDate(user.getCreationDate());
	    newUser.setSoundEnabled(true);
	    Set<Role> roleSet= Arrays.asList(roles).stream().map(role -> roleRepository.findByName(role)).collect(Collectors.toSet());
	    newUser.setRoles(roleSet);
        newUser = userRepository.save(newUser);
        //Arrays.asList(new EloScore[] {bulletElo}
        
        EloScore bulletElo=eloScoreRepository.save(new EloScore(newUser,user.getEloRatings()[0], user.getCreationDate()));
        EloScore blitzElo=eloScoreRepository.save(new EloScore(newUser,user.getEloRatings()[1], user.getCreationDate()));
        EloScore rapidElo=eloScoreRepository.save(new EloScore(newUser,user.getEloRatings()[2], user.getCreationDate()));
        
        newUser.setBulletEloScores(new ArrayList<>(Arrays.asList(new EloScore[]{bulletElo})));
        newUser.setBlitzEloScores(new ArrayList<>(Arrays.asList(new EloScore[]{blitzElo})));
        newUser.setRapidEloScores(new ArrayList<>(Arrays.asList(new EloScore[]{rapidElo})));
        newUser.setTheme(user.getTheme());
        userRepository.save(newUser);
        return newUser;
        
    }
	
	public User signUp(UserDto user,String... roles) throws UserException {
		if (!userRepository.findById(user.getUsername()).isEmpty()) {
			throw new AccountExistsException();
		} else {
			user.setTheme(Themes.DEFAULT);
			user.setCreationDate(Calendar.getInstance());
			return this.save(user, roles);
		}
			
	}
	
    @Transactional
	public UserDto updateAvatar(MultipartFile avatarFile,String username) throws Exception {
		User user= this.findOne(username); 
		Avatar avatar=avatarRepository.save(new Avatar(avatarFile.getContentType(),avatarFile.getBytes(),user));
		user.setAvatar(avatar);
		userRepository.save(user);
		return new UserDto(user);
		
	}
}
