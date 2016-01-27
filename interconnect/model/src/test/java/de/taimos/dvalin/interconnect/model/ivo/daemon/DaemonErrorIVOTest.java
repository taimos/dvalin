package de.taimos.dvalin.interconnect.model.ivo.daemon;

import org.junit.Assert;
import org.junit.Test;

import de.taimos.dvalin.interconnect.model.InterconnectMapper;
import de.taimos.dvalin.interconnect.model.InterconnectObject;


@SuppressWarnings("javadoc")
public final class DaemonErrorIVOTest {

	@Test
	public void testICO() throws Exception {
		final DaemonErrorIVO in = new DaemonErrorIVO.DaemonErrorIVOBuilder().build();
		final String json = InterconnectMapper.toJson(in);
		final InterconnectObject out = InterconnectMapper.fromJson(json);
		Assert.assertEquals(in.getClass().getCanonicalName(), out.getClass().getCanonicalName());
	}

}
