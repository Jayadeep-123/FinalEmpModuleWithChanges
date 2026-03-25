package com.employee.entity;

import jakarta.persistence.Column;
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
@Table(name="sce_stream", schema="sce_course")
public class Stream {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "stream_id")
	    private Integer streamId;

	    @Column(name = "stream_name", nullable = false, length = 50)
	    private String streamName;

	    @Column(name = "stream_colour", length = 50)
	    private String streamColour;

	    @Column(name = "test_stream")
	    private Integer testStream;

//	    @Column(name = "course_track_id")
//	    private Integer courseTrackId;

	    @Column(name = "onsite_reg")
	    private Integer onsiteReg;

	    @Column(name = "is_active")
	    private Integer isActive; 

}
