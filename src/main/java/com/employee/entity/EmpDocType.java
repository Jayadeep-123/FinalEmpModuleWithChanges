package com.employee.entity;
 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="sce_doc_type",schema="sce_employee")
public class EmpDocType {
	@Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int  doc_type_id;
	private String doc_name;
	private String doc_type;
	private String doc_short_name;
	
	@jakarta.persistence.Column(name = "description")
	private String description; // Fixed typo: was "descciption", now "description"
	
	private int is_active;
	
 
}