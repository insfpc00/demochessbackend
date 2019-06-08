package es.fporto.demo.minichess.user.controller;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.fporto.demo.minichess.controller.model.ChangePasswordRequest;
import es.fporto.demo.minichess.controller.model.MatchHistoryStatsResponse;
import es.fporto.demo.minichess.controller.model.UpdateUserRequest;
import es.fporto.demo.minichess.controller.model.UserStats;
import es.fporto.demo.minichess.model.MatchStats;
import es.fporto.demo.minichess.model.TimeControlType;
import es.fporto.demo.minichess.stats.StatsService;
import es.fporto.demo.minichess.user.model.User;
import es.fporto.demo.minichess.user.model.UserDto;
import es.fporto.demo.minichess.user.service.AccountExistsException;
import es.fporto.demo.minichess.user.service.UserException;
import es.fporto.demo.minichess.user.service.UserService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private StatsService statsService;

	private static final String USER_ROLE = "USER";
	private static final double INITIAL_ELO = 1500d;

	public enum TimeInterval {
		DAY, WEEK, MONTH, YEAR;
	}

	@Autowired
	private UserService userService;

	@PostMapping(value = "/signup")
	public ResponseEntity<User> signUp(@RequestBody UserDto userDto) throws AuthenticationException {
		userDto.setEloRatings(new Double[] { INITIAL_ELO, INITIAL_ELO, INITIAL_ELO });
		User user;
		try {
			user = userService.signUp(userDto, USER_ROLE);
		} catch (AccountExistsException e) {

			return new ResponseEntity<User>(HttpStatus.CONFLICT);

		} catch (UserException e) {
			return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@GetMapping
	public UserDto get(Principal principal) {
		User user = userService.findOne(principal.getName());
		return new UserDto(user);
	}

	@PutMapping(value = "/password")
	public void changePassword(@RequestBody ChangePasswordRequest request, Principal principal) throws Exception {

		userService.changePassword(principal.getName(), request.getOldPassword(), request.getNewPassword());

	}

	@PutMapping
	public UserDto update(@RequestBody UpdateUserRequest request, Principal principal) throws Exception {
		System.out.println(request.getSoundEnabled());
		return userService.update(principal.getName(), request.getTitle(), request.getTheme(), request.getCountry(),
				request.getSoundEnabled());

	}

	@PutMapping(value = "/avatar")
	public void updateAvatar(@RequestParam(name = "avatar") MultipartFile avatarFile, Principal principal)
			throws Exception {

		userService.updateAvatar(avatarFile, principal.getName());

	}

	@GetMapping("/matches/stats")
	public MatchHistoryStatsResponse getMatchesStats(@RequestParam("groupBy") Optional<TimeInterval> groupBy,
			@RequestParam("until") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Calendar> until,
			@RequestParam("count") Optional<Integer> count, Principal principal) {
		List<MatchStats> stats;
		if (until.isPresent() && count.isPresent() && groupBy.isPresent()) {
			Calendar from = this.computeFromDate(groupBy.get(), count.get(), until.get());
			stats = statsService.getMatchHistoryStats(principal.getName(), from, until.get());

		} else {
			stats = statsService.getOverallMatchHistoryStats(principal.getName());
		}

		return new MatchHistoryStatsResponse(this.reduce(stats, TimeControlType.RAPID),
				this.reduce(stats, TimeControlType.BLITZ), this.reduce(stats, TimeControlType.BULLET));

	}

	private MatchStats reduce(List<MatchStats> matchStatsList, TimeControlType timeControl) {
		
		final Function<Integer, Boolean> isBlitz = d -> d >= 300 && d < 600;
		final Function<Integer, Boolean> isBullet = d -> d < 300;
		final Function<Integer, Boolean> isRapid = d -> d >= 600;

		final Map<TimeControlType, Function<Integer, Boolean>> controlTypeConditionsMap = Map.of(TimeControlType.BLITZ,
				isBlitz, TimeControlType.BULLET, isBullet, TimeControlType.RAPID, isRapid);

		Optional<MatchStats> reducedMatchStats = matchStatsList.stream()
				.filter(s -> controlTypeConditionsMap.get(timeControl).apply(s.getTimeInSeconds()))
				.reduce((s1, s2) -> new MatchStats(s1.getMatchesWonAsWhite() + s2.getMatchesWonAsWhite(),
						s1.getMatchesWonAsBlack() + s2.getMatchesWonAsBlack(),
						s1.getDrawMatches() + s2.getDrawMatches(), s1.getTotalMatches() + s2.getTotalMatches(),
						s1.getTimeInSeconds()));

		if (reducedMatchStats.isEmpty()) {
			int duration = timeControl == TimeControlType.RAPID ? 600
					: timeControl == TimeControlType.BLITZ ? 300 : 180;
			return new MatchStats(0, 0, 0, 0, duration);
		} else {
			return reducedMatchStats.get();
		}
	}

	@GetMapping("/elo/stats")

	public UserStats getEloStats(@RequestParam("groupBy") TimeInterval groupBy,
			@RequestParam("until") @DateTimeFormat(pattern = "yyyy-MM-dd") Calendar until,
			@RequestParam("count") int count, Principal principal) {

		Calendar from = this.computeFromDate(groupBy, count, until);

		return statsService.getEloStats(principal.getName(), groupBy, from, until);
	}

	private Calendar computeFromDate(TimeInterval groupBy, int numberOfResults, Calendar until) {

		Calendar from = (Calendar) until.clone();

		numberOfResults--;

		switch (groupBy) {
		case DAY:
			from.add(Calendar.DATE, -numberOfResults);
			break;
		case WEEK:
			from.set(Calendar.DAY_OF_WEEK, 0);
			from.add(Calendar.WEEK_OF_YEAR, -numberOfResults);
			break;
		case MONTH:
			from.set(Calendar.DAY_OF_MONTH, 0);
			from.add(Calendar.MONTH, -numberOfResults);
			break;
		case YEAR:
			from.set(Calendar.DAY_OF_YEAR, 0);
			from.add(Calendar.YEAR, -numberOfResults);
			break;

		}

		return from;

	}

}
