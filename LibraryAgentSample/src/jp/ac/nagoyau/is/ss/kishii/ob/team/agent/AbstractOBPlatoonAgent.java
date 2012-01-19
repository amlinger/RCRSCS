package jp.ac.nagoyau.is.ss.kishii.ob.team.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.nagoyau.is.ss.kishii.ob.network.SuntoriNode;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Action;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.Util;
import jp.ac.nagoyau.is.ss.kishii.ob.route.ASter;
import jp.ac.nagoyau.is.ss.kishii.ob.team.AbstractOBAgent;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.AmbulanceTeamInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.BlockadeInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.BuildingInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.FireBrigadeInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.PoliceForceInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.PositionInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.TransferInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.UnpassableInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.VictimInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.WorldInformation;
import rescuecore2.messages.Command;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.Edge;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.PoliceForce;
import rescuecore2.standard.entities.Road;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public abstract class AbstractOBPlatoonAgent<E extends Human> extends
		AbstractOBAgent<E> {
	protected List<EntityID> chargeBuildings;
	protected List<EntityID> chargeRoads;
	protected double chargeAreaId;
	protected final static int SEPARATED_SIZE = 36;

	protected Action previousAction;
	protected Pair<Integer, Integer> myLocation;
	protected Pair<Integer, Integer> prevLocation;
	protected EntityID myPosition;
	protected int positionCount;

	protected List<EntityID> path;
	protected List<EntityID> transfer;

	protected List<EntityID> firstFreePlatoon;
	protected Collection<EntityID> visibles;
	protected List<EntityID> reachableAreaList;
	private Map<EntityID, SuntoriNode> nodeMap;

	public AbstractOBPlatoonAgent() {
		super();
		this.chargeBuildings = new ArrayList<EntityID>();
		this.chargeRoads = new ArrayList<EntityID>();
		this.previousAction = Action.REST;
		this.path = null;
		this.myPosition = null;
		this.myLocation = null;
		this.visibles = new ArrayList<EntityID>();
		this.positionCount = 0;
		this.reachableAreaList = new ArrayList<EntityID>();
		this.nodeMap = new HashMap<EntityID, SuntoriNode>();
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		this.firstFreePlatoon = new ArrayList<EntityID>(this.belongPlatoon);
		arrangeChargeArea();
		if (this.router instanceof ASter) {
			this.nodeMap = ((ASter) this.router).getNodeMap();
		}
	}

	@Override
	protected final void thinking(int time, ChangeSet arg1,
			Collection<Command> arg2) {
		super.thinking(time, arg1, arg2);
		// if (time > this.constants.START_ACTION_TIME + 1) {
		// if (this.previousAction == Action.MOVE
		// && this.myPosition.equals(this.me().getPosition())) {
		// this.positionCount++;
		// if (this.positionCount >= 5) {
		// List<EntityID> path = this.router.getRoute(this.myPosition,
		// this.refugeList);
		// if (path == null || path.size() == 0) {
		// path = this.router.getRoute(this.myPosition,
		// ((Area) this.model.getEntity(this.myPosition))
		// .getNeighbours());
		// }
		// this.move(time, path);
		// this.positionCount = 0;
		// return;
		// }
		// } else {
		// this.positionCount = 0;
		// }
		// }
		this.updateMyData();
		this.visibles = arg1.getChangedEntities();
		this.arrangeInformation(time, arg1);
		// 担当領域の再分配
		if (time == this.constants.START_ACTION_TIME + 1) {
			for (EntityID id : this.platoonList) {
				if (((Human) this.model.getEntity(id)).getBuriedness2() > 0) {
					this.firstFreePlatoon.remove(id);
				}
			}
			this.roleIndex = this.firstFreePlatoon.indexOf(this.myID);
			if (this.roleIndex >= 0) {
				this.setChargeArea2();
			}
			arrangeChargeArea();
		}
		// 再分配が終わったら行動開始
		if (time >= this.constants.START_ACTION_TIME + 1) {
			// どのエリアが到達可能であるかを計算
			this.calcReachable();

			// routerの更新
			this.updateRouter();
			E self = this.me();
			int hp = self.getHP2();
			int damage = 0;
			if (self.isDamageDefined()) {
				damage = self.getDamage();
			}
			// ダメージが深刻な場合は避難所で回復
			if (this.refugeList.contains(this.myPosition) && damage > 0) {
				this.sendRest(time);
				this.previousAction = Action.REST;
				return;
			}
			if (damage >= 150 || (hp > 1000 && hp < 2000 && damage > 40)
					|| (hp < 1000 && damage > 0)) {
				if (moveToRefuge(time)) {
					return;
				}
			}
			// 移動ルートに現在位置が登録されていて，かつ移動距離があまりに小さいときは閉塞の存在を考慮
			if (this.previousAction == Action.MOVE
					&& this.path != null
					&& this.path.contains(this.myPosition)
					&& Util.getDistance(this.prevLocation, this.myLocation) <= 2000) {
				// int index = this.path.indexOf(this.myPosition);
				// if (index + 1 < this.path.size()) {// toがあるかどうか
				// EntityID from = this.path.get(index);
				// EntityID to = this.path.get(index + 1);
				// this.checkBlockadeInTheWay(arg1, from, to);
				// }

				// 周りのAreaに関して到達可能であるかどうかを確認
				Area standArea = (Area) this.model.getEntity(this.myPosition);
				for (EntityID id : standArea.getNeighbours()) {
					this.checkBlockadeInTheWay(time, arg1, this.myPosition, id);
				}
			}
			// 体力に問題がないか避難所に移動不可能と判断されたとき。
			thinking2(time, arg1, arg2);
		}
		// if (arg0 >= this.constants.START_ACTION_TIME) {
		// thinking2(arg0, arg1, arg2);
		// }
	}

	private void checkInSiteRoad() {
		List<EntityID> checked = new ArrayList<EntityID>();
		Area standArea = (Area) this.model.getEntity(this.myPosition);
		for (EntityID id : standArea.getNeighbours()) {
			if (this.visibles.contains(id)) {

			}
			checked.add(id);
		}
	}

	private void updateRouter() {
		if (this.path != null && this.path.contains(this.myPosition)) {
			for (int i = 1; i <= this.path.indexOf(this.myPosition); i++) {
				this.router.setPassable(this.path.get(i - 1), this.path.get(i));
			}
		}
	}

	protected abstract void arrangeChargeArea();

	protected abstract void thinking2(int arg0, ChangeSet arg1,
			Collection<Command> arg2);

	protected boolean moveToRefuge(int time) {
		return this.moveToAnyGoal(time, this.refugeList);
	}

	protected boolean moveToAnyGoal(int time, List<EntityID> col) {
		boolean res = false;
		this.path = this.router.getRoute(this.myPosition, col);
		if (this.path != null && this.path.size() > 0) {
			super.sendMove(time, this.path);
			this.previousAction = Action.MOVE;
			res = true;
		}
		return res;
	}

	/**
	 * 自分の担当する領域を計算する．
	 */
	private void setChargeArea2() {
		this.chargeBuildings = new ArrayList<EntityID>();
		this.chargeRoads = new ArrayList<EntityID>();
		double s = (double) SEPARATED_SIZE / this.firstFreePlatoon.size();
		int areaStartId = (int) (s * this.roleIndex);
		int areaEndId = (int) (s * (this.roleIndex + 1));
		int areaEdge = (int) ((areaStartId + SEPARATED_SIZE - 1) % SEPARATED_SIZE);
		chargeAreaId = areaStartId;

		double centerX = (this.model.getWorldBounds().second().first() - this.model
				.getWorldBounds().first().first()) / 2;
		double centerY = (this.model.getWorldBounds().second().second() - this.model
				.getWorldBounds().first().second()) / 2;
		double baseDirection = 2 * Math.PI / SEPARATED_SIZE;
		// 建物
		for (StandardEntity se : this.model.getEntitiesOfType(
				StandardEntityURN.BUILDING, StandardEntityURN.REFUGE)) {
			double direction = Util.radian(centerX, centerY,
					se.getLocation(this.model));
			int group = (int) Math.floor(direction / baseDirection);
			if (group >= areaStartId && group <= areaEndId || group == areaEdge) {
				this.chargeBuildings.add(se.getID());
			}
		}
		if (!(me() instanceof PoliceForce)) {
			this.chargeBuildings.removeAll(this.refugeList);
		}
		// 道路
		for (StandardEntity se : this.model
				.getEntitiesOfType(StandardEntityURN.ROAD)) {
			double direction = Util.radian(centerX, centerY,
					se.getLocation(this.model));
			int group = (int) Math.floor(direction / baseDirection);
			if (group >= areaStartId && group <= areaEndId || group == areaEdge) {
				this.chargeRoads.add(se.getID());
			}
		}
	}

	protected void updateMyData() {
		E self = this.me();
		this.prevLocation = this.myLocation;
		this.myLocation = self.getLocation(this.model);
		this.myPosition = self.getPosition();
	}

	/**
	 * routerを使って移動経路を計算．<br>
	 * startはmyPositionを使用する．<br>
	 * 
	 * @param time
	 * @param goal
	 * @return 経路が見つかった場合はtrue<br>
	 *         otherwise:false
	 */
	protected boolean move(int time, EntityID goal) {
		boolean res = false;
		// 経路が無い場合はnullになる．
		this.path = router.getRoute(this.myPosition, goal);
		if (this.path != null && this.path.size() > 0) {
			this.sendMove(time, this.path);
			this.previousAction = Action.MOVE;
			res = true;
		}
		return res;
	}

	/**
	 * moveを行う
	 * 
	 * @param time
	 * @param dest
	 */
	protected boolean move(int time, List<EntityID> path) {
		boolean res = false;
		this.path = path;
		if (this.path != null && this.path.size() > 0) {
			super.sendMove(time, this.path);
			this.previousAction = Action.MOVE;
			res = true;
		}
		return res;
	}

	protected boolean move(int time, EntityID dest, int x, int y) {
		boolean res = false;
		this.path = this.router.getRoute(this.me().getPosition(), dest);
		if (this.path != null && this.path.size() > 0) {
			super.sendMove(time, this.path, x, y);
			this.previousAction = Action.MOVE;
			res = true;
		}
		return res;
	}

	protected boolean rest(int time) {
		this.sendRest(time);
		this.previousAction = Action.REST;
		return true;
	}

	/**
	 * 受信メッセージの整理や視覚情報をaddMessageするためのメソッド
	 */
	protected void arrangeInformation(int time, ChangeSet changed) {
		// 視覚情報に関する整理
		for (EntityID id : changed.getChangedEntities()) {
			StandardEntity se = this.model.getEntity(id);
			if (se instanceof Building) {
				this.addBuildingInformation(time, (Building) se);
			} else if (se instanceof Blockade) {
				this.addBlockadeInformation(time, (Blockade) se);
			} else if (se instanceof Civilian) {
				// this.addVictimInformation(time, (Civilian) se);
			}
		}
		// 自身の情報
		this.setSelfDataToMessage(time);

		// 受信した情報に関する整理
		this.arrangeTasks(time);
		this.arrangeMessage(changed);
		// 啓開隊以外
		if (!(this.me() instanceof PoliceForce)) {
			// for (UnpassableInformation info : this.unpassableInfoList) {
			// EntityID from = info.getFromAreaID();
			// EntityID to = info.getToAreaID();
			// if (from != null && to != null) {
			// this.router.setUnpassable(from, to);
			// }
			// }
			// } else {
			// // 通行要請の登録
			// for (UnpassableInformation info : this.unpassableInfoList) {
			// EntityID from = info.getFromAreaID();
			// EntityID to = info.getToAreaID();
			// if (from != null && to != null) {
			// this.clearReqestSet.add(new NodeConnection(from, to));
			// }
			// }
		}
		// 視野情報から燃え尽きた建物を取得
		// for (EntityID id : visible) {
		// StandardEntity se = this.model.getEntity(id);
		// if (se instanceof Building) {
		// Building b = (Building) se;
		// if (b.getFieryness2() == 8) {
		// this.burnOutBuildingSet.add(id);
		// // this.burningBuildingSet.remove(id);
		// // } else if (!b.isOnFire()) {
		// // this.burningBuildingSet.remove(id);
		// // } else {
		// // this.burningBuildingSet.add(id);
		// }
		// }
		// }
	}

	protected abstract void setSelfDataToMessage(int time);

	protected abstract void arrangeTasks(int time);

	private void addUnpassableInformation(int time, EntityID from, EntityID to,
			EntityID blockID) {
		this.addMessage(new UnpassableInformation(time, this.myID, from, to,
				blockID));
	}

	private void addBuildingInformation(int time, Building b) {
		BuildingInformation info = new BuildingInformation(time, b.getID(),
				b.getFieryness2(), b.getBrokenness2());
		this.addMessage(info);
	}

	private void addBlockadeInformation(int time, Blockade b) {
		BlockadeInformation info = new BlockadeInformation(time, b.getID(),
				b.getPosition(), b.getRepairCost());
		// BlockadeInformation info = new BlockadeInformation(time, b.getID(),
		// b.getPosition(), b.getRepairCost(), b.getLocation(this.model));
		this.addMessage(info);
	}

	private void addVictimInformation(int time, Civilian civ) {
		EntityID position = civ.getPosition();
		if (this.areaList.contains(position) && civ.getBuriedness2() > 0) {
			VictimInformation info = new VictimInformation(time, civ.getID(),
					position, civ.getHP2(), civ.getBuriedness2(),
					civ.getDamage());
			this.addMessage(info);
		}
	}

	protected void addAmbulanceTeamInformation(int time, AmbulanceTeam at) {
		AmbulanceTeamInformation info = new AmbulanceTeamInformation(time,
				at.getID(), at.getHP2(), at.getDamage(), at.getBuriedness2(),
				at.getPosition());
		this.addMessage(info);
	}

	protected void addFireBrigadeInformation(int time, FireBrigade fb) {
		FireBrigadeInformation info = new FireBrigadeInformation(time,
				fb.getID(), fb.getHP2(), fb.getDamage(), fb.getBuriedness2(),
				fb.getWater2(), fb.getPosition());
		this.addMessage(info);
	}

	protected void addPoliceForceInformation(int time, PoliceForce pf) {
		PoliceForceInformation info = new PoliceForceInformation(time,
				pf.getID(), pf.getHP2(), pf.getDamage(), pf.getBuriedness2(),
				pf.getPosition());
		this.addMessage(info);
	}

	/**
	 * 自分の移動の軌跡情報をメッセージにセットする．
	 * 
	 * @param time
	 */
	protected void setTransferData(int time) {
		if (this.path != null && this.path.size() > 0) {
			int index = this.path.indexOf(this.myPosition);
			if (index != -1) {
				this.transfer = new ArrayList<EntityID>(this.path.subList(0,
						index + 1));
				this.addMessage(new TransferInformation(time, this.myID,
						this.transfer));
			}
		}
	}

	/**
	 * 初めて取得する情報をmodelに追加する．<br>
	 * 救助隊，建物などは初期データで登録されているので，ここでは行わない．<br>
	 * よって，ここでは閉塞と市民の登録が行われる．
	 * 
	 * @param message
	 */
	protected void addNewData(WorldInformation message) {
		if (message instanceof BlockadeInformation) {
			BlockadeInformation info = (BlockadeInformation) message;
			EntityID blockID = info.getBlockadeID();
			Blockade blockade = new Blockade(blockID);
			blockade.setPosition(info.getRoadID());
			blockade.setRepairCost(info.getRepairCost());
			if (info.getMessageType().equals(
					BaseMessageType.BLOCKADE_WITH_COORDINATE)) {
				Pair<Integer, Integer> coodinate = info.getCoodinate();
				blockade.setX(coodinate.first());
				blockade.setY(coodinate.second());
			}
			StandardEntity se = this.model.getEntity(info.getRoadID());
			if (se instanceof Road) {
				List<EntityID> blocks = ((Road) se).getBlockades();
				if (blocks == null) {
					blocks = new ArrayList<EntityID>();
				}
				if (!blocks.contains(blockID)) {
					blocks.add(blockID);
				}
			}
			this.model.addEntity(blockade);
		} else if (message instanceof VictimInformation) {
			VictimInformation info = (VictimInformation) message;
			Civilian civilian = new Civilian(info.getVictimID());
			civilian.setBuriedness(info.getBuriedness());
			civilian.setDamage(info.getDamage());
			civilian.setHP(info.getHP());
			civilian.setPosition(info.getAreaID());
			if (info.getMessageType().equals(
					BaseMessageType.VICTIM_WITH_COORDINATE)) {
				Pair<Integer, Integer> coodinate = info.getCoodinate();
				civilian.setX(coodinate.first());
				civilian.setY(coodinate.second());
			}
			this.model.addEntity(civilian);
		}
	}

	/**
	 * modelの情報を更新する．
	 * 
	 * @param message
	 * @param se
	 */
	protected void updateData(WorldInformation message, StandardEntity se) {
		if (message instanceof BlockadeInformation) {
			BlockadeInformation info = (BlockadeInformation) message;
			Blockade blockade = (Blockade) se;
			blockade.setRepairCost(info.getRepairCost());
		} else if (message instanceof BuildingInformation) {
			BuildingInformation info = (BuildingInformation) message;
			Building building = (Building) se;
			building.setBrokenness(info.getBrokenness());
			building.setFieryness(info.getFieryness());
			// if (building.getFieryness2() == 8) {
			// this.burnOutBuildingSet.add(building.getID());
			// }
		} else if (message instanceof AmbulanceTeamInformation) {
			AmbulanceTeamInformation info = (AmbulanceTeamInformation) message;
			AmbulanceTeam at = (AmbulanceTeam) se;
			at.setBuriedness(info.getBuriedness());
			at.setDamage(info.getDamage());
			at.setHP(info.getHP());
			at.setPosition(info.getPositionID());
			// this.platoonPositionList.add(info.getPositionID());
		} else if (message instanceof FireBrigadeInformation) {
			FireBrigadeInformation info = (FireBrigadeInformation) message;
			FireBrigade fb = (FireBrigade) se;
			fb.setBuriedness(info.getBuriedness());
			fb.setDamage(info.getDamage());
			fb.setHP(info.getHP());
			fb.setPosition(info.getPositionID());
			// this.platoonPositionList.add(info.getPositionID());
		} else if (message instanceof PoliceForceInformation) {
			PoliceForceInformation info = (PoliceForceInformation) message;
			PoliceForce pf = (PoliceForce) se;
			pf.setBuriedness(info.getBuriedness());
			pf.setDamage(info.getDamage());
			pf.setHP(info.getHP());
			pf.setPosition(info.getPositionID());
			// this.platoonPositionList.add(info.getPositionID());
		} else if (message instanceof PositionInformation) {
			PositionInformation info = (PositionInformation) message;
			Human h = (Human) se;
			Pair<Integer, Integer> coodinate = info.getCoordinate();
			h.setX(coodinate.first());
			h.setY(coodinate.second());
		} else if (message instanceof TransferInformation) {
			TransferInformation info = (TransferInformation) message;
			List<EntityID> pathway = info.getPathway();
			if (pathway != null && pathway.size() >= 2) {
				EntityID from = pathway.get(0);
				for (int i = 1; i < pathway.size(); i++) {
					EntityID to = pathway.get(i);
					this.router.setPassable(from, to);
					// if (this.myID.getValue() == 35387) {
					// System.out.println("passable:" + this.path.get(i - 1)
					// + "," + this.path.get(i));
					// }
					// this.clearReqestSet.remove(new NodeConnection(from, to));
					from = to;
				}
			}
		} else if (message instanceof VictimInformation) {
			VictimInformation info = (VictimInformation) message;
			Civilian civ = (Civilian) se;
			civ.setBuriedness(info.getBuriedness());
			civ.setDamage(info.getDamage());
			civ.setHP(info.getHP());
			civ.setPosition(info.getAreaID());
			if (info.getMessageType().equals(
					BaseMessageType.VICTIM_WITH_COORDINATE)) {
				Pair<Integer, Integer> coodinate = info.getCoodinate();
				civ.setX(coodinate.first());
				civ.setY(coodinate.second());
			}
		}
	}

	/**
	 * あるエリアAの中心から隣接エリアBの中心に向かうのに邪魔な閉塞があるかを調べる．<br>
	 * 邪魔な閉塞が存在する場合はその閉塞による通行不可を登録する．<br>
	 * 調べる方法としては，<br>
	 * まず自分の居場所(A内のどこか)からBとの接合部分までの線分上Cに閉塞が存在するかどうかを調べる．<br>
	 * 無ければ<br>
	 * CからBの中心までの線分上に隣接エリアが存在するかを調べる．<br>
	 * このとき，視野範囲外の部分は過去の情報などに左右されてしまう可能性があるので注意が必要．(視覚情報のみを使用することで回避)
	 * 
	 * @param fromId
	 * @param toId
	 */
	protected void checkBlockadeInTheWay(int time, ChangeSet changed,
			EntityID fromId, EntityID toId) {
		if (!this.myPosition.equals(fromId)) {// 自分の現在位置がfromIdと一致することが最低条件
			return;
		}
		// 検索対象の閉塞を計算
		List<Blockade> fromBlockList = new ArrayList<Blockade>();
		List<Blockade> toBlockList = new ArrayList<Blockade>();
		for (EntityID id : changed.getChangedEntities()) {
			StandardEntity se = this.model.getEntity(id);
			if (se instanceof Blockade) {
				Blockade b = (Blockade) se;
				EntityID position = b.getPosition();
				if (position.equals(fromId)) {
					fromBlockList.add(b);
				} else if (position.equals(toId)) {
					toBlockList.add(b);
				}
			}
		}
		Area fromArea = (Area) this.model.getEntity(fromId);
		Area toArea = (Area) this.model.getEntity(toId);
		Pair<Integer, Integer> toAreaLocation = toArea.getLocation(this.model);
		// 接続エッジの取得
		Edge edge = fromArea.getEdgeTo(toId);
		// 接続エッジの中点計算
		Pair<Integer, Integer> centerOfEdge = Util.getCenterOf(
				edge.getStartX(), edge.getStartY(), edge.getEndX(),
				edge.getEndY());
		if (Util.isInOrCrossingBlockade(this.myLocation, centerOfEdge,
				fromBlockList)) {
			this.router.setUnpassable(fromId, toId);
		} else if (Util.isInOrCrossingBlockade(centerOfEdge, toAreaLocation,
				toBlockList)) {
			this.router.setUnpassable(fromId, toId);
		} else {
			List<Blockade> blockList = new ArrayList<Blockade>(fromBlockList);
			blockList.addAll(toBlockList);
			if (Util.isInOrCrossingBlockade(this.myLocation, toAreaLocation,
					blockList)) {
				this.router.setUnpassable(fromId, toId);
			} else {
				this.router.setPassable(fromId, toId);
			}
		}
	}

	/**
	 * 到達可能であると考えられるエリアの計算
	 */
	protected void calcReachable() {
		if (this.nodeMap != null) {
			Set<EntityID> set = new HashSet<EntityID>();
			Set<EntityID> finished = new HashSet<EntityID>();
			List<EntityID> unchecked = new ArrayList<EntityID>();
			unchecked.add(this.myPosition);
			while (unchecked.size() > 0) {
				EntityID id = unchecked.get(0);
				set.add(id);
				unchecked.remove(id);
				finished.add(id);
				SuntoriNode node = this.nodeMap.get(id);
				if (node == null) {
					continue;
				}
				List<EntityID> passable = node.getPassable();
				passable.removeAll(finished);
				passable.removeAll(unchecked);
				unchecked.addAll(passable);
			}
			this.reachableAreaList = new ArrayList<EntityID>(set);
		} else {
			this.reachableAreaList = new ArrayList<EntityID>(this.areaList);
		}
	}

}
