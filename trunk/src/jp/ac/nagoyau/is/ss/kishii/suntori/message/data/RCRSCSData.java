package jp.ac.nagoyau.is.ss.kishii.suntori.message.data;

public abstract class RCRSCSData<E extends Object> {
	protected DataType type;
	protected E value;

	RCRSCSData(DataType type) {
		this.type = type;
		this.value = null;
	}

	// RCRSCSData(DataType type, E value) {
	// this.type = type;
	// this.value = null;
	// }

	public DataType getType() {
		return this.type;
	}

	public E getData() {
		return this.value;
	}

	public abstract void setData(E obj);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RCRSCSData other = (RCRSCSData) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.type.toString() + "[" + this.value.toString() + "]";
	}
}
