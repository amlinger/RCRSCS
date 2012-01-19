package jp.ac.nagoyau.is.ss.kishii.ob.team.center;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.nagoyau.is.ss.kishii.ob.primitive.FireSite;
import jp.ac.nagoyau.is.ss.kishii.ob.primitive.SuntoriBuilding;
import jp.ac.nagoyau.is.ss.kishii.ob.team.AbstractOBAgent;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.AmbulanceTeamInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.BlockadeInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.BuildingInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.FireBrigadeInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.PoliceForceInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.PositionInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.TransferInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.VictimInformation;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.information.WorldInformation;
import rescuecore2.messages.Command;
import rescuecore2.misc.Pair;
import rescuecore2.standard.entities.AmbulanceTeam;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Civilian;
import rescuecore2.standard.entities.FireBrigade;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.PoliceForce;
import rescuecore2.standard.entities.Road;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.worldmodel.ChangeSet;
import rescuecore2.worldmodel.EntityID;

public abstract class AbstractOBCenter<E extends StandardEntity> extends
		AbstractOBAgent<E> {
	protected List<SuntoriBuilding> sbList;
	protected List<FireSite> fireSites;
	protected Map<SuntoriBuilding, FireSite> siteMap;
	protected Map<EntityID, FireSite> idSiteMap;

	public AbstractOBCenter() {
		super();
		this.sbList = new ArrayList<SuntoriBuilding>();
		this.fireSites = new ArrayList<FireSite>();
		this.siteMap = new HashMap<SuntoriBuilding, FireSite>();
		this.idSiteMap = new HashMap<EntityID, FireSite>();
	}

	@Override
	protected void postConnect() {
		super.postConnect();
		this.sbList = SuntoriBuilding.createSuntoriBuildings(this.model);
		this.fireSites = FireSite.createFireSites(this.model, sbList);
		for (int i = 0; i < this.fireSites.size(); i++) {
			FireSite fs0 = this.fireSites.get(i);
			for (int j = i; j < this.fireSites.size(); j++) {
				FireSite fs1 = this.fireSites.get(j);
				if (fs0.distance(fs1, this.model) < 150 * 000) {
					fs0.getSiteWithin150m().add(fs1);
					fs1.getSiteWithin150m().add(fs0);
				}
			}
			for (SuntoriBuilding sb : fs0.getBuildings()) {
				this.siteMap.put(sb, fs0);
				this.idSiteMap.put(sb.getID(), fs0);
			}
		}
		for (SuntoriBuilding sb : this.sbList) {
			FireSite fs = this.siteMap.get(sb.getID());
			for (SuntoriBuilding sb2 : sb.getDirectConnectBuilding()) {
				FireSite fs2 = this.siteMap.get(sb2.getID());
				if (this.fireSites.indexOf(fs) != this.fireSites.indexOf(fs2)) {
					fs.getConnectionFireSite().add(fs2);
					fs2.getConnectionFireSite().add(fs);
				}
			}
		}
	}

	@Override
	protected final void thinking(int arg0, ChangeSet arg1,
			Collection<Command> arg2) {
		super.thinking(arg0, arg1, arg2);
		this.arrangeMessage(arg1);
		if (arg0 >= this.constants.START_ACTION_TIME) {
			thinking2(arg0, arg1, arg2);
		}
	}

	protected abstract void thinking2(int time, ChangeSet changed,
			Collection<Command> heard);

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
}
