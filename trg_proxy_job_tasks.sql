/******************************************************************************
 * NAME:		 trg_proxy_job_tasks.sql
 * AUTHOR:		 Sumit Jain
 * DATE:		 July 27, 2009
 *
 *
 * DESCRIPTION:	 Trigger trg_proxy_job_tasks is fired when an update 
 *               or delete is performed on table proxy_job_tasks.
 *               The values of modified record of the proxy_job_tasks
 *               table are stored into  appropriate variables declared.
 *               The contents of these variables are then inserted
 *			     into  proxy_job_tasks_hist table.
 *				
 * REVISIONS:
 * _____________________________________________________________________________
 *
 * 	$Archive: $
 *	$Author: $
 *	$Date: $
 *	$Revision: $
 *
 * _____________________________________________________________________________
 *
 ******************************************************************************/  

	CREATE OR REPLACE TRIGGER ORADBA.trg_proxy_job_tasks
	  AFTER UPDATE OR DELETE ON ORADBA.proxy_job_tasks
	   FOR EACH ROW
	  
   	   DECLARE 
             	
    /* These variables will hold previous values of field of 
      	proxy_job_tasks table */
    
			task_id				int(8);		
			job_attribute_id 	int(8);		
			sequence_number 	int(8);		
			created_on 			TIMESTAMP(6); 	
			created_by			VARCHAR(128);	
			modlast_on			TIMESTAMP(6);
		  	modlast_by			VARCHAR(128);  	
		  	          
       BEGIN
       	
        /* Store old.job_attribute_id,old.task_id etc into job_attribute_id
          and task_id. These variable can be used to insert data into the
          proxy_job_tasks_hist table. */ 
                    
      	   set task_id	   		 := :old.task_id;
      	  set job_attribute_id := :old.job_attribute_id;		
		  	set sequence_number  := :old.sequence_number; 	
    		set created_on 		 := :old.created_on;	
			set created_by		 := :old.created_by;	
			set modlast_on		 := :old.modlast_on;	
			set modlast_by		 := :old.modlast_by;
		
		/* Insert data into the proxy_job_tasks_hist table. */ 
		
		INSERT INTO ORADBA.proxy_job_tasks_hist
		(	 updt_dtti,
			 task_id,
			 job_attribute_id,
			 sequence_number,
			 created_on,
			 created_by,
			 modlast_on,
			 modlast_by
		)
	   VALUES
	   (	sysdate,
			task_id,
			job_attribute_id,
			sequence_number,
			created_on,
		    created_by,
		 	modlast_on,
		 	modlast_by
	   );
		
	
		
    END;
    /
    
 /*
 *	$Log: $
 *
 */
    