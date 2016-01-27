package de.taimos.dvalin.interconnect.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.taimos.dvalin.interconnect.model.ivo.daemon.DaemonErrorIVO;

@SuppressWarnings("javadoc")
public final class InterconnectListTest {

	@Test
	public void testSerialization() throws Exception {
		final List<DaemonErrorIVO> elements = new ArrayList<>();
		elements.add(new DaemonErrorIVO.DaemonErrorIVOBuilder().build());
		elements.add(new DaemonErrorIVO.DaemonErrorIVOBuilder().build());
		elements.add(new DaemonErrorIVO.DaemonErrorIVOBuilder().build());
		final InterconnectList<DaemonErrorIVO> icl = new InterconnectList<>(elements);
		final String json = InterconnectMapper.toJson(icl);
		System.out.println(json);
		final InterconnectObject ico = InterconnectMapper.fromJson(json);
		Assert.assertEquals(InterconnectList.class, ico.getClass());
	}
}
