package jp.ac.nagoyau.is.ss.kishii.ob.primitive;

import rescuecore2.misc.Pair;
import rescuecore2.worldmodel.EntityID;

/**
 * 消火点候補のエリアIDと消火可能範囲の重心座標のペアを表すクラス
 * 
 * @author takefumi
 * 
 */
public class ExtinguishPoint {
	private EntityID areaID;
	private int x;
	private int y;

	public ExtinguishPoint(EntityID areaID, int x, int y) {
		this.areaID = areaID;
		this.x = x;
		this.y = y;
	}

	public ExtinguishPoint(EntityID areaID, Pair<Integer, Integer> coodinate) {
		this(areaID, coodinate.first(), coodinate.second());
	}

	public EntityID getAreaID() {
		return this.areaID;
	}

	public Pair<Integer, Integer> getCoodinate() {
		return new Pair<Integer, Integer>(this.x, this.y);
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}
}
