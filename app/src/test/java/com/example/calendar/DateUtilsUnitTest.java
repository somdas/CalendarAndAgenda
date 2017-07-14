package com.example.calendar;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DateUtilsUnitTest {

    @Mock
    Context mMockContext;

    @Before
    public void init() {
        when(mMockContext.getString(R.string.short_sunday))
                .thenReturn("Sun");
        when(mMockContext.getString(R.string.short_january))
                .thenReturn("Jan");
    }

    @Test
    public void testFormattedDatePositive() throws Exception {
        String str = DateTimeUtils.formattedDate(mMockContext, 1, 0, 1, 2017);
        assertThat(str, is("Sun, Jan 1, 2017"));
    }

    @Test
    public void testFormattedDateNegative() throws Exception {
        String str = DateTimeUtils.formattedDate(mMockContext, 1, 0, 1, 2017);
        assertThat(str, not("Sun, Jan 01, 2017"));
    }

    @Test
    public void testDurationString() throws Exception {
        java.util.Calendar start = java.util.Calendar.getInstance();
        java.util.Calendar end = (java.util.Calendar) start.clone();
        end.add(java.util.Calendar.HOUR, 1);

        String str = DateTimeUtils.getDurationInFormattedString(start, end);
        assertThat(str, is("1 h"));

        end.add(java.util.Calendar.MINUTE, 20);
        str = DateTimeUtils.getDurationInFormattedString(start, end);
        assertThat(str, is("1 h 20 m"));
    }

    @Test
    public void testDurationInDays() throws Exception {
        java.util.Calendar start = java.util.Calendar.getInstance();
        java.util.Calendar end = (java.util.Calendar) start.clone();
        end.add(java.util.Calendar.DATE, 3);

        int res = DateTimeUtils.getDurationInDays(start, end);
        assertThat(res, is(3));

        end.add(java.util.Calendar.HOUR, 20);
        res = DateTimeUtils.getDurationInDays(start, end);
        assertThat(res, is(3));
    }

    @Test
    public void testFormattedTime() throws Exception {
        String res = DateTimeUtils.formattedTime(15, 37).toString();
        assertThat(res, is("03:37 PM"));
        res = DateTimeUtils.formattedTime(0, 0).toString();
        assertThat(res, is("00:00 AM"));
    }

    @Test
    public void testParseDate() throws Exception {
        java.util.Calendar cal = DateTimeUtils.parseDate(mMockContext, "Sun, Jan 1, 2017");
        assertThat(cal.get(java.util.Calendar.DAY_OF_MONTH), is(1));
        assertThat(cal.get(java.util.Calendar.MONTH), is(0));
        assertThat(cal.get(java.util.Calendar.YEAR), is(2017));
    }
}