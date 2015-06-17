package org.springframework.data.hadoop.hbase;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * HBase Data Access exception.
 * 
 * @author Costin Leau
 */
@SuppressWarnings("serial")
public class HbaseSystemException extends UncategorizedDataAccessException {

	public HbaseSystemException(Exception cause) {
		super(cause.getMessage(), cause);
	}
}