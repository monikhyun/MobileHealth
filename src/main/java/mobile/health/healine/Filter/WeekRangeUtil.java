package mobile.health.healine.Filter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeekRangeUtil {

    /**
     * 기준 날짜를 포함하는 주차 범위를 반환한다.
     * 주차 기준은 '일요일 ~ 토요일'이다.
     *
     * @param date 기준 날짜 (예: LocalDate.now())
     * @return [0] = 시작일(일요일), [1] = 종료일(토요일)
     */
    public static LocalDate[] getWeekRangeSundayToSaturday(LocalDate date) {
        // 월=1 ~ 일=7
        int dayOfWeek = date.getDayOfWeek().getValue();
        int offsetFromSunday = dayOfWeek % 7; // 일요일=0, 월=1, ..., 토=6

        LocalDate sunday = date.minusDays(offsetFromSunday);
        LocalDate saturday = sunday.plusDays(6);

        return new LocalDate[]{sunday, saturday};
    }

    public static LocalDate[] getThisWeekRange() {
        return getWeekRangeSundayToSaturday(LocalDate.now());
    }

    public static List<LocalDate[]> getWeekRangesOfMonth(LocalDate date) {
        List<LocalDate[]> weekRanges = new ArrayList<>();

        // 1) 해당 월의 1일, 말일
        LocalDate monthStart = date.withDayOfMonth(1);
        LocalDate monthEnd = date.withDayOfMonth(date.lengthOfMonth());

        // 2) 월의 첫 번째 날짜(1일)에서 가장 가까운(같거나 이전) 일요일 구하기
        int firstDayOfWeekValue = monthStart.getDayOfWeek().getValue(); // MONDAY=1 ... SUNDAY=7
        int offsetToSunday = firstDayOfWeekValue % 7; // SUNDAY=7%7=0, MONDAY=1%7=1, ...
        LocalDate currentSunday = monthStart.minusDays(offsetToSunday);

        // 3) 그 주차가 “해당 월”과 겹치는 동안 반복
        while (!currentSunday.isAfter(monthEnd)) {
            LocalDate currentSaturday = currentSunday.plusDays(6);

            // 주차가 월과 “겹치기만 하면” 포함
            if (!(currentSaturday.isBefore(monthStart) || currentSunday.isAfter(monthEnd))) {
                weekRanges.add(new LocalDate[]{ currentSunday, currentSaturday });
            }
            currentSunday = currentSunday.plusDays(7);
        }

        return weekRanges;
    }

    public static List<LocalDate[]> getCurrentAndPreviousTwoMonthRanges(LocalDate date) {
        List<LocalDate[]> result = new ArrayList<>();

        // ----- 0) 해당 월 -----
        LocalDate currentMonth = date;
        LocalDate startCurrent = currentMonth.withDayOfMonth(1);
        LocalDate endCurrent   = currentMonth.withDayOfMonth(currentMonth.lengthOfMonth());
        result.add(new LocalDate[]{ startCurrent, endCurrent });

        // ----- 1) 1달 전 -----
        LocalDate oneMonthAgo = date.minusMonths(1);
        LocalDate startOne    = oneMonthAgo.withDayOfMonth(1);
        LocalDate endOne      = oneMonthAgo.withDayOfMonth(oneMonthAgo.lengthOfMonth());
        result.add(new LocalDate[]{ startOne, endOne });

        // ----- 2) 2달 전 -----
        LocalDate twoMonthsAgo = date.minusMonths(2);
        LocalDate startTwo     = twoMonthsAgo.withDayOfMonth(1);
        LocalDate endTwo       = twoMonthsAgo.withDayOfMonth(twoMonthsAgo.lengthOfMonth());
        result.add(new LocalDate[]{ startTwo, endTwo });

        return result;
    }
}