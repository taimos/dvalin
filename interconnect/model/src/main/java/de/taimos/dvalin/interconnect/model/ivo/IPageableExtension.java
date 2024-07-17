package de.taimos.dvalin.interconnect.model.ivo;

import java.util.List;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public interface IPageableExtension extends IPageable {

    /**
     * @return a list of fields to sort by and their direction
     */
    List<PageableSort> getSortExtension();
}
