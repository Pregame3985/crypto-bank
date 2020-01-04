package com.mcb.address.manager.entity;

import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author william
 */
@Setter
@MappedSuperclass
public abstract class AbstractEntity<ID extends Serializable> extends AbstractPersistableEntity<ID> {

}
