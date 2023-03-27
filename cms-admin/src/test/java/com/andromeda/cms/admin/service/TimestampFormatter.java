package com.andromeda.cms.admin.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class TimestampFormatter {

	//public static void main(String[] args) throws ParseException {
		/*OffsetDateTime odt = OffsetDateTime.parse( "2022-09-11T02:09:50.109Z" );
		Instant instant = odt.toInstant();
		java.sql.Timestamp ts = java.sql.Timestamp.from( instant );
		
		String input = ts.toString();  // Strongly recommend using 4-digit year whenever possible, as suggested in Meno Hochschild’s Answer.
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd hh:mm:ss.SSS" ); // No need for Locale in this case, but always consider it.
		LocalDateTime localDateTime = LocalDateTime.parse( input , formatter );

		ZoneId zoneId_plus_5_30 = ZoneId.of( "GMT+05:30" );
		ZonedDateTime zonedDateTimeKolkata = ZonedDateTime.of( localDateTime , zoneId_plus_5_30 );
		System.out.println( "zonedDateTimeKolkata: " + zonedDateTimeKolkata );*/
		
		
		/*String inputValue = "2022-09-19T11:47:56.787Z";
	    Instant timestamp = Instant.parse(inputValue);
	    ZonedDateTime zonedDateTimeKolkata = timestamp.atZone(ZoneId.of("+05:30"));
	    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
	    String formattedString2 = zonedDateTimeKolkata.format(formatter2);
	    System.out.println(formattedString2);

	}*/
	
	public static void main(String[] args) throws ParseException {
		String englishTitle = "Hello World --@- please";
		String updatedEnglishTitle = englishTitle.trim().toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "");
		updatedEnglishTitle = updatedEnglishTitle.replaceAll("\\s+", "-");
		System.out.println(updatedEnglishTitle);
	}
	
}