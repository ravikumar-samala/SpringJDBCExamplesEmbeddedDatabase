package edu.ku.it.si.springjdbcexamples.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import edu.ku.it.si.springjdbcexamples.dao.PersonDao;
import edu.ku.it.si.springjdbcexamples.model.Person;



@ContextConfiguration //if no location is specified will look for TestPersonDao-context.xml in this package
@RunWith(SpringJUnit4ClassRunner.class)
public class TestPersonDao {

	@Autowired
	PersonDao personDao ;
	
	private static final Logger logger = Logger.getLogger(TestPersonDao.class.getName() );

	@Test
	public void testGetNumberOfPeople() {
		
		int numberOfPeople = personDao.getNumberOfPeople();
		
		logger.info("Number of people found in table is " + numberOfPeople);
		
		assertEquals("Number of people in table is not 1", 1, numberOfPeople);
		
	}
	
	@Test
	public void testGetFirstName() {
		
		String firstName = personDao.getFirstName("Phillips");
		
		logger.info("First name of LG is " + firstName);
		
		assertEquals("First name not Bruce", "Bruce", firstName);
		
	}
	
	@Test
	public void testAddPerson() {
		
		String firstName = "Kit";
		String lastName = "Cole";
		
		personDao.addPerson(firstName, lastName);
		
        int numberOfPeople = personDao.getNumberOfPeople();
		
		logger.info("Number of people found in table is " + numberOfPeople);
		
		assertEquals("Number of people in table is not 2", 2, numberOfPeople);
		
	}
	
	
	@Test
	public void testGetAllPersons() {
		
		List<Person> people = personDao.getAllPersons();
		
		logger.info("The number of people in the table is " + people.size() );
		
		assertEquals("Number of people in table is not 2", 2, people.size() );
		
		
	}
	
	
	
	

}
