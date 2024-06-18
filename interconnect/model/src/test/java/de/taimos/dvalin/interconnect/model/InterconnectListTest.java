package de.taimos.dvalin.interconnect.model;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
		Assertions.assertEquals(InterconnectList.class, ico.getClass());
	}
}
