package com.simply.schedule

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses


/**
 * Runs all UI tests.
 */
@RunWith(Suite::class)
@SuiteClasses(
    FailedRegistrationTest::class,
    SuccessfulRegistrationTest::class,
    WrongPasswordLoginTest::class,
    WrongUsernameLoginTest::class,
    SuccessfulAuthTest::class,
    ChangeDayTest::class,
    AddTeacherTest::class
    )
class UITestSuite