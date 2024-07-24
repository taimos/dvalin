package de.taimos.dvalin.daemon;

import de.taimos.daemon.spring.SpringDaemonExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@ExtendWith(SpringDaemonExtension.class)
class ExtensionTest extends AbstractTest {

    @Test
    void testDemo() {
        Assertions.assertEquals("is here", super.getDemo());
    }
}
