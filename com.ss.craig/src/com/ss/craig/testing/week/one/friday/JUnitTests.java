/**
 * 
 */
package com.ss.craig.testing.week.one.friday;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/** 
 * @author Craig Saunders
 */
 @RunWith(Suite.class)
 @SuiteClasses({DateTimeAPIExamplesTest.class,
     InputUtilityTest.class,
     LambdaSingletonTest.class
     //LambdaStreamsTest.class,
     //ProgramTest.class
     })
public class JUnitTests {
    
}
