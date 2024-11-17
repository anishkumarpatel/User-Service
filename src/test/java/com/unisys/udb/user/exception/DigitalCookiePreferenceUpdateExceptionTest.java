package com.unisys.udb.user.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DigitalCookiePreferenceUpdateExceptionTest {

    @Test
    void testConstructor() {
        Throwable e = new Throwable();
        DigitalCookiePreferenceUpdateException actualDigitalCookiePreferenceUpdateException = new
                DigitalCookiePreferenceUpdateException("An error occurred", e);

        assertEquals("An error occurred", actualDigitalCookiePreferenceUpdateException.getLocalizedMessage());
        assertEquals("An error occurred", actualDigitalCookiePreferenceUpdateException.getMessage());
        Throwable cause = actualDigitalCookiePreferenceUpdateException.getCause();
        assertNull(cause.getLocalizedMessage());
        assertNull(cause.getMessage());
        assertNull(cause.getCause());
        Throwable[] suppressed = actualDigitalCookiePreferenceUpdateException.getSuppressed();
        assertEquals(0, suppressed.length);
        assertSame(e, cause);
        assertSame(suppressed, cause.getSuppressed());
    }
}
