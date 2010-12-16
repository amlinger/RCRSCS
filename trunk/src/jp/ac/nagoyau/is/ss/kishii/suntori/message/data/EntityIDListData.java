package jp.ac.nagoyau.is.ss.kishii.suntori.message.data;

import java.util.ArrayList;
import java.util.List;

import rescuecore2.worldmodel.EntityID;

public class EntityIDListData extends RCRSCSData<List<EntityID>> {

	public EntityIDListData(DataType type, List<EntityID> value) {
		super(type);
		this.value = new ArrayList<EntityID>(value);
	}

	public EntityIDListData(DataType type) {
		super(type);
		this.value = new ArrayList<EntityID>();
	}

	@Override
	public void setData(List<EntityID> obj) {
		this.value = new ArrayList<EntityID>(obj);
	}

	public void setData(EntityID obj) {
		this.value.add(obj);
	}
}
