package org.springframework.data.hadoop.hbase;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.springframework.util.Assert;

/**
 * Adapter encapsulating the RowMapper callback. 
 * 
 * @author Costin Leau
 */
class RowMapperGetResultsExtractor<T> implements GetResultsExtractor<List<T>> {

	private final RowMapper<T> rowMapper;

	/**
	 * Create a new RowMapperResultSetExtractor.
	 * @param rowMapper the RowMapper which creates an object for each row
	 */
	public RowMapperGetResultsExtractor(RowMapper<T> rowMapper) {
		Assert.notNull(rowMapper, "RowMapper is required");
		this.rowMapper = rowMapper;
	}

	public List<T> extractData(Result[]results) throws Exception {
		List<T> rs = new ArrayList<T>();
		int rowNum = 0;
		for (Result result : results) {
			rs.add(this.rowMapper.mapRow(result, rowNum++));
		}
		return rs;
	}
}