package com.employee.dto;




import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FamilyDetailsDTO {
	
	
		
		/**
		 * These 7 fields match the 7 parameters in your
		 * FamilyDetailsRepository query, in the same order.
		 */
		
		// 1. From rel.relationName
		private String relation;      // e.g., "Father"
		
		// 2. From CONCAT(fd.first_name, ' ', fd.last_name)
		private String name;          // e.g., "Name of Father"
		
		// 3. From bg.bloodGroupName
		private String bloodGroup;    // e.g., "A-"
		
		// 4. From g.genderName
		private String gender;        // e.g., "Male"
		
		// 5. From fd.nationality
		private String nationality;   // e.g., "Indian"
		
		// 6. From fd.occupation
		private String occupation;    // e.g., "IT Job"
		
		// 7. From fd.date_of_birth
		private Date dateOfBirth;

	

}
