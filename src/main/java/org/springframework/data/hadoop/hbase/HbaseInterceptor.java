package org.springframework.data.hadoop.hbase;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.springframework.util.Assert;

/**
 * AOP interceptor that binds a new Hbase table to the thread before a method call, closing and removing it afterwards in case of any method outcome.
 * If there is already a pre-bound table (from a previous call or transaction), the interceptor simply participates in it.
 * Typically used alongside {@link HbaseSynchronizationManager}.
 *
 * @author Costin Leau
 */
public class HbaseInterceptor extends HbaseAccessor implements MethodInterceptor {

	private static final Log log = LogFactory.getLog(HbaseInterceptor.class);

	private boolean exceptionConversionEnabled = true;
	private String[] tableNames;


	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		Assert.notEmpty(tableNames, "at least one table needs to be specified");
	}

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		Set<String> boundTables = new LinkedHashSet<String>();

		for (String tableName : tableNames) {
			if (!HbaseSynchronizationManager.hasResource(tableName)) {
				boundTables.add(tableName);
				HTableInterface table = HbaseUtils.getHTable(tableName, getConfiguration(), getCharset(), getTableFactory());
				HbaseSynchronizationManager.bindResource(tableName, table);
			}
		}

		try {
			Object retVal = methodInvocation.proceed();
			return retVal;
		} catch (Exception ex) {
			if (this.exceptionConversionEnabled) {
				throw convertHBaseException(ex);
			}
			else {
				throw ex;
			}
		} finally {
			for (String tableName : boundTables) {
				HTableInterface table = (HTableInterface) HbaseSynchronizationManager.unbindResourceIfPossible(tableName);
				if (table != null) {
					HbaseUtils.releaseTable(tableName, table);
				}
				else {
					log.warn("Table [" + tableName + "] unbound from the thread by somebody else; cannot guarantee proper clean-up");
				}
			}
		}
	}

	private Exception convertHBaseException(Exception ex) {
		return HbaseUtils.convertHbaseException(ex);
	}

	public void setTableNames(String[] tableNames) {
		this.tableNames = tableNames;
	}

	/**
	 * Sets whether to convert any {@link IOException} raised to a Spring DataAccessException,
	 * compatible with the <code>org.springframework.dao</code> exception hierarchy.
	 * <p>Default is "true". Turn this flag off to let the caller receive raw exceptions
	 * as-is, without any wrapping.
	 * @see org.springframework.dao.DataAccessException
	 *
	 * @param exceptionConversionEnabled enable exceptionConversion
	 */
	public void setExceptionConversionEnabled(boolean exceptionConversionEnabled) {
		this.exceptionConversionEnabled = exceptionConversionEnabled;
	}
}
