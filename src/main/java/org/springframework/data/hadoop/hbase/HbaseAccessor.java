package org.springframework.data.hadoop.hbase;

import java.nio.charset.Charset;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTableInterfaceFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Base class for {@link HbaseTemplate} and {@link HbaseInterceptor}, defining commons properties such as {@link HTableInterfaceFactory} and {@link Configuration}.
 * 
 * Not intended to be used directly.
 * 
 * @author Costin Leau
 */
public abstract class HbaseAccessor implements InitializingBean {

	private String encoding;
	private Charset charset = HbaseUtils.getCharset(encoding);

	private HTableInterfaceFactory tableFactory;
	private Configuration configuration= HBaseConfiguration.create();

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(configuration, " a valid configuration is required");
		// detect charset
		charset = HbaseUtils.getCharset(encoding);
	}

	/**
	 * Sets the table factory.
	 *
	 * @param tableFactory The tableFactory to set.
	 */
	public void setTableFactory(HTableInterfaceFactory tableFactory) {
		this.tableFactory = tableFactory;
	}

	/**
	 * Sets the encoding.
	 *
	 * @param encoding The encoding to set.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Sets the configuration.
	 *
	 * @param configuration The configuration to set.
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public Charset getCharset() {
		return charset;
	}

	public HTableInterfaceFactory getTableFactory() {
		return tableFactory;
	}

	public Configuration getConfiguration() {
		return configuration;
	}
}
