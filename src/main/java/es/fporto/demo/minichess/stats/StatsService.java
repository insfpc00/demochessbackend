package es.fporto.demo.minichess.stats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.fporto.demo.minichess.controller.model.UserStats;
import es.fporto.demo.minichess.model.MatchStats;
import es.fporto.demo.minichess.repository.EloScoreRepository;
import es.fporto.demo.minichess.repository.MatchRepository;
import es.fporto.demo.minichess.repository.UserRepository;
import es.fporto.demo.minichess.user.controller.UserController;
import es.fporto.demo.minichess.user.controller.UserController.TimeInterval;
import es.fporto.demo.minichess.user.model.EloScore;

@Service
public class StatsService {

	@Autowired
	private EloScoreRepository eloScoreRepository;
	@Autowired
	private MatchRepository matchRepository;
	@Autowired 
	private UserRepository userRepository;

	public UserStats getEloStats(String username, UserController.TimeInterval groupBy, Calendar from, Calendar until) {
		
		Calendar userCreationDate=truncateCalendar(userRepository.findByUsername(username).getCreationDate());
		
		Calendar truncatedFrom = truncateCalendar((Calendar) from.clone());
		
		Calendar truncatedUntil = (Calendar) until.clone();
		truncatedUntil.add(Calendar.DATE, 1);
		truncatedUntil = truncateCalendar(truncatedUntil);
		
		if (from.before(userCreationDate)) {
			truncatedFrom = userCreationDate;
		}
		
		List<EloScore> bulletELoScores = eloScoreRepository.findBulletEloRatings(username, truncatedFrom, until);
		List<EloScore> blitzEloScores = eloScoreRepository.findBlitzEloRatings(username, truncatedFrom, until);
		List<EloScore> rapidEloScores = eloScoreRepository.findRapidEloRatings(username, truncatedFrom, until);
		
		UserStats result = new UserStats();
		result.setTimeUnit(groupBy.toString());

		result.setBlitzEloRatings(this.computeStats(blitzEloScores, groupBy, truncatedFrom, truncatedUntil));
		result.setBulletEloRatings(this.computeStats(bulletELoScores, groupBy, truncatedFrom, truncatedUntil));
		result.setRapidEloRatings(this.computeStats(rapidEloScores, groupBy, truncatedFrom, truncatedUntil));

		result.setLabels(this.computeLabels(truncatedFrom, truncatedUntil, groupBy));

		return result;

	}
	
	public List<MatchStats> getMatchHistoryStats(String username, Calendar since, Calendar until) {
		
		Calendar clonedUntil= (Calendar) until.clone();
		clonedUntil.add(Calendar.DATE, 1);
		return matchRepository.getStatsByUser(username, since, clonedUntil);
	
	}
	
	public List<MatchStats> getOverallMatchHistoryStats(String username) {
		
		return matchRepository.getOverallStatsByUser(username);
	
	}
	

	private String[] computeLabels(Calendar from, Calendar until, UserController.TimeInterval groupBy) {
		// String pattern = "yyyy-MM-dd";

		Calendar fromCal = (Calendar) from.clone();
		int increment = 0;
		String formatPattern = null;

		switch (groupBy) {
		case DAY:
			increment = Calendar.DAY_OF_YEAR;
			formatPattern = "yyyy-MM-dd";
			break;
		case WEEK:
			increment = Calendar.WEEK_OF_YEAR;
			formatPattern = "yyyy-MM-dd";
			break;
		case MONTH:
			increment = Calendar.MONTH;
			formatPattern = "yyyy-MM";
			break;
		case YEAR:
			increment = Calendar.YEAR;
			formatPattern = "yyyy";
			break;
		}

		List<String> result = new ArrayList<String>();
		while (fromCal.before(until) || fromCal.equals(until)) {
			result.add(formatCalendar(fromCal, formatPattern));
			fromCal.add(increment, 1);
		}

		return result.toArray(new String[result.size()]);
	}

	private Double[] computeStats(List<EloScore> eloScoreList, UserController.TimeInterval groupBy, Calendar from,
			Calendar until) {
		List<Double> result = new ArrayList<Double>();
		Calendar fromCal = (Calendar) from.clone();
		Calendar untilCal = (Calendar) until.clone();

		int currentIntervalDays = 0;
		int lastEloIndex = 0;
		double lastEloValue = eloScoreList.get(lastEloIndex++).getEloRating();
		double sum = 0d;
		
		while (fromCal.before(untilCal) || fromCal.equals(untilCal)) {

			if (eloScoreList.size() > lastEloIndex && eloScoreList.get(lastEloIndex).getDate().equals(fromCal)) {

				lastEloValue = eloScoreList.get(lastEloIndex++).getEloRating();
			}

			sum += lastEloValue;
			currentIntervalDays++;

			if (intervalChanges(fromCal, groupBy)) {
				result.add(sum / currentIntervalDays);
				sum = 0d;
				currentIntervalDays = 0;

			}
			fromCal.add(Calendar.DATE, 1);
		}

		return result.toArray(new Double[result.size()]);
	}

	private boolean intervalChanges(Calendar c1, TimeInterval groupBy) {

		Calendar c2 = (Calendar) c1.clone();
		c2.add(Calendar.DATE, +1);
		boolean result = false;

		switch (groupBy) {
		case DAY:
			result = c1.get(Calendar.DAY_OF_YEAR) != c2.get(Calendar.DAY_OF_YEAR);
			break;
		case WEEK:
			result = c1.get(Calendar.WEEK_OF_YEAR) != c2.get(Calendar.WEEK_OF_YEAR);
			break;
		case MONTH:
			result = c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH);
			break;

		case YEAR:
			break;
		}

		return result || c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR);

	}

	private String formatCalendar(Calendar cal, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(cal.getTimeZone());
		String date = simpleDateFormat.format(cal.getTime());
		return date;
	}

	private Calendar truncateCalendar(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

}
