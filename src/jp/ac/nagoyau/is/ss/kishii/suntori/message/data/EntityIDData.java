package jp.ac.nagoyau.is.ss.kishii.suntori.message.data;

import rescuecore2.worldmodel.EntityID;

public class EntityIDData extends RCRSCSData<EntityID> {

	public EntityIDData(DataType type, EntityID value) {
		super(type);
		this.value = value;
	}

	// public EntityIDData(DataType type) {
	// super(type);
	// this.value = null;
	// }

	@Override
	public void setData(EntityID obj) {
		this.value = obj;
	}
}
