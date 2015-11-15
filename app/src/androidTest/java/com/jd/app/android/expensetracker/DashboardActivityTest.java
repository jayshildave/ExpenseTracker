package com.jd.app.android.expensetracker;

import junit.framework.TestCase;

/**
 * Created by jayshildave on 14/11/15.
 */
public class DashboardActivityTest extends TestCase {

    public void testAdd() throws Exception {
        assertEquals(1, new DashboardActivity().add());
    }
}