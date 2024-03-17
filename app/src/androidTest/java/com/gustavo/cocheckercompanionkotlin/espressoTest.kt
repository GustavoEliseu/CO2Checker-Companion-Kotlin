package com.gustavo.cocheckercompanionkotlin

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gustavo.cocheckercompanionkotlin.ui.main.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SimpleEspressoTest {

    @get : Rule
    var mActivityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        //initial setup code
    }

    @Test
    fun clickForAddData() {
    }

    @After
    fun tearDown() {
        //clean up code
    }
}