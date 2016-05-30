package edu.ku.it.si.springjdbcexamples.dao;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;


import edu.ku.it.si.springjdbcexamples.model.Person;

/**
 * Defines behaviors for getting Person information out of the relational
 * database
 * 
 * @author brucephillips
 * 
 */
public class PersonDaoImplDB extends SimpleJdbcDaoSupport implements PersonDao {
	
	

	@Override
	public List<String> getLastNames() {
			
		
		String queryStr = "SELECT personLastName FROM personTBL ";

		List<String> lastNames = this.getSimpleJdbcTemplate().query(

		queryStr,

		new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String lastName = new String();

				lastName = rs.getString(1);

				return lastName;
			}
		}

		); // end query method call

		return lastNames;

	}

	@Override
	public int getNumberOfPeople() {
		
		int rowCount = this.getJdbcTemplate().queryForInt("select count(0) from personTbl");
		
		return rowCount;
	}

	@Override
	public String getFirstName(String lastName) {
		
		String firstName = (String) this.getJdbcTemplate().queryForObject(
		        "select personFirstName from personTbl where personLastName = ?", 
		        new Object[]{lastName}, String.class);
		
		return firstName;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Person getPerson(String lastName) {

		Person person = (Person) this.getJdbcTemplate().queryForObject(
			    "select personId, personFirstName, personLastName from personTbl where personLastName = ?",
			    new Object[]{lastName},
			    new RowMapper() {

			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			            Person person = new Person();
			            person.setPersonFirstName(rs.getString("personFirstName"));
			            person.setPersonLastName(rs.getString("personLastName"));
			            person.setPersonID(rs.getInt("personId"));
			            return person;
			        }
			    });
		
		return person;
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> getPeople() {

		List<Person> people = this.getJdbcTemplate().query(
			    "select personID, personFirstName, personLastName from personTbl",
			    new RowMapper() {
                    //note return type is Object due to not using SimpleJDBCTemplate
			    	//and Java 5's covariant return type
 			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			            Person person = new Person();
			            person.setPersonFirstName(rs.getString("personFirstName"));
			            person.setPersonLastName(rs.getString("personLastName"));
			            person.setPersonID(rs.getInt("personId"));
			            return person;
			        }
			    });
		
		return people;
	}

	@Override
	public void addPerson(String firstName, String lastName) {

		this.getJdbcTemplate().update(
		        "insert into personTbl (personFirstName, personLastName) values (?, ?)", 
		        new Object[] {firstName, lastName});
		
	}

	@Override
	public void updatePerson(Person person) {

		this.getJdbcTemplate().update(
		        "update personTbl set personFirstName = ?, personLastName = ? where personID = ?", 
		        new Object[] {person.getPersonFirstName(), person.getPersonLastName(), new Integer(person.getPersonID() )});
		
	}

	@Override
	public void deletePerson(Person person) {

		this.getJdbcTemplate().update(
		        "delete from personTbl where personId = ?",
		        new Object[] {new Integer(person.getPersonID()) });
		
	}

	@Override
	public void doDBCleanUp() {

		 this.getJdbcTemplate().update(
		        "delete from personTbl where personId > ?",
		        new Object[] {new Integer(4) });
		
		
		
	}

	@Override
	public int countPeopleWithFirstName(String firstName) {

		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.getDataSource());
		
		String sql = "select count(0) from personTbl where personFirstName = :first_name";

	    SqlParameterSource namedParameters = new MapSqlParameterSource("first_name", firstName);

	    return namedParameterJdbcTemplate.queryForInt(sql, namedParameters);

		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Person> getPerson(Person personSkeleton) {

		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.getDataSource());

		SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(personSkeleton);

		List<Person> people = namedParameterJdbcTemplate.query(
			    "select personID, personFirstName, personLastName from personTbl " + 
			    " where personLastName = :personLastName and personFirstName = :personFirstName",
			    
			    namedParameters,
			    
			    new RowMapper() {

			        public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			            Person person = new Person();
			            person.setPersonFirstName(rs.getString("personFirstName"));
			            person.setPersonLastName(rs.getString("personLastName"));
			            person.setPersonID(rs.getInt("personId"));
			            return person;
			        }
			    });
		
		return people;
		
	}

	@Override
	public Person getPerson(int personId) {

		String sql = "select personId, personFirstName, personLastName from personTbl where personID = ?";

	    

	    return this.getSimpleJdbcTemplate().queryForObject(sql, new PersonMapper(), personId);

		
	}

	@Override
	public List<Map<String,Object>> getPeopleList() {
		
		 return this.getSimpleJdbcTemplate().queryForList("select * from personTbl");
		
	}

	@Override
	public void addPerson(Person newPerson) {

		SimpleJdbcInsert insertPerson = 
			new SimpleJdbcInsert(this.getDataSource())
		     .withTableName("personTbl").usingGeneratedKeyColumns("personID");


        Map<String, Object> parameters = new HashMap<String, Object>(3);
        parameters.put("personFirstName", newPerson.getPersonFirstName() );
        parameters.put("personLastName", newPerson.getPersonLastName() );
        insertPerson.execute(parameters);


	}

	@Override
	public List<Person> getAllPersons() {
		
		String sql = "select personId, personFirstName, personLastName from personTbl";


	    List <Person> people = this.getSimpleJdbcTemplate().query(sql, new PersonMapper());
	    
	    return people;

		
	}
	
	/**
	 * A static inner class to encapsulate the creation of a Person
	 * object for each row of the ResultSet
	 * @author brucephillips
	 *
	 */
	private static final class PersonMapper implements RowMapper<Person> {

		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
       	 Person person = new Person();
	         person.setPersonFirstName(rs.getString("personFirstName"));
	         person.setPersonLastName(rs.getString("personLastName"));
	         person.setPersonID(rs.getInt("personId"));
	         return person;
       }
	}
	
	

}
