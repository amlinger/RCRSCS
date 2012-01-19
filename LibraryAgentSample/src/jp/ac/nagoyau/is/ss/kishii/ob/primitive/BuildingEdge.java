package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import rescuecore2.standard.entities.Edge;

public class BuildingEdge {
	Edge edge;

	int startX;
	int startY;
	int endX;
	int endY;
	SuntoriBuilding b;

	double distance;

	int counter = 0;

	public BuildingEdge(Edge edge, SuntoriBuilding b) {
		this.edge = edge;
		this.startX = edge.getStartX();
		this.startY = edge.getStartY();
		this.endX = edge.getEndX();
		this.endY = edge.getEndY();
		this.b = b;
	}

	public Edge getEdge() {
		return this.edge;
	}

	public SuntoriBuilding getOwner() {
		return this.b;
	}

	public double getDistance() {
		return this.distance;
	}

	public boolean isCross(long destX, long destY, long destX2, long destY2,
			long baseX, long baseY) {
		long x1 = (long) this.startX;
		long x2 = (long) this.endX;
		long y1 = (long) this.startY;
		long y2 = (long) this.endY;

		// 交点交差判定(分からなかったらgoogle先生)
		long v0 = (y1 - y2) * (destX - x1) - (x1 - x2) * (destY - y1);
		long v1 = (y1 - y2) * (destX2 - x1) - (x1 - x2) * (destY2 - y1);
		long v2 = (destY - destY2) * (x1 - destX) - (destX - destX2)
				* (y1 - destY);
		long v3 = (destY - destY2) * (x2 - destX) - (destX - destX2)
				* (y2 - destY);

		// if(v0*v1<0&&v2*v3<0){//交点をもとめる。間違ってたらごめん。
		if (((v0 > 0 && v1 < 0) || (v0 < 0 && v1 > 0))
				&& ((v2 > 0 && v3 < 0) || (v2 < 0 && v3 > 0))) {
			double x = 0;
			double y = 0;
			if (destX2 == destX && x1 == x2) {// 2つの線分がy軸に対して垂直の時
				double dy1 = y1 - baseY;
				double dy2 = y2 - baseY;
				if (dy1 * dy2 <= 0) {// どうやらここには来ていない
					distance = 0;
				} else {
					if (Math.abs(dy1) > Math.abs(dy2)) {
						distance = dy2;
					} else {
						distance = dy1;
					}
				}
				// distance = 0;// 本来はこっちだった
				return true;
			}
			double a = 0.0, b = 0.0;
			if (x1 != x2) {
				// この輪郭の傾き
				a = ((double) (y2 - y1)) / (x2 - x1);
			}
			if (destX2 != destX) {
				// 引数として与えられた線分の傾き
				b = ((double) (destY2 - destY)) / (destX2 - destX);
			}
			if (a == b) {
				return false;
			}
			if ((x1 != x2) && (destX2 != destX)) {
				x = ((double) ((x1 * a) - y1 - (destX * b) + destY)) / (a - b);
				y = a * (x - x1) + y1;
			} else if ((x1 == x2) && (destX != destX2)) {
				x = x1;
				y = b * (x - destX2) + destY2;
			} else if ((x1 != x2) && (destX == destX2)) {
				x = destX2;
				y = a * (x - x1) + y1;
			}

			double dx = x - baseX;
			double dy = y - baseY;

			// cx = x;
			// cy = y;
			// System.out.println(x+"-"+y);
			distance = Math.sqrt(dx * dx + dy * dy);
			if (dy < 0 || (dy == 0 && dx < 0)) {
				distance *= -1;
			}
			return true;
		} else {
			return false;
		}
	}

	public void addCounter() {
		this.counter++;
	}

	public int getCounter() {
		return this.counter;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((edge == null) ? 0 : edge.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BuildingEdge other = (BuildingEdge) obj;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (edge == null) {
			if (other.edge != null)
				return false;
		} else if (!edge.equals(other.edge))
			return false;
		return true;
	}
}
