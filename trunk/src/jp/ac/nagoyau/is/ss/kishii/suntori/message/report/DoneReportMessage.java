package jp.ac.nagoyau.is.ss.kishii.suntori.message.report;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import rescuecore2.worldmodel.EntityID;

/**
 * タスクが完了したことを報告するためのメッセージクラスです．<br>
 * The DoneReportMessage report that the given instruction is completed.
 * 
 * @author takefumi
 * 
 */
public class DoneReportMessage extends ReportMessage {

	/**
	 * コンストラクタ<br>
	 * タスク完了報告メッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>platoonID:救助隊のEntityID</li>
	 * </ul>
	 * <h2>Constructor</h2> Create the message that report the completion of
	 * given task. <br>
	 * Included data are follow.<br>
	 * <ul>
	 * <li>time:the time that the message is created.(int)</li>
	 * <li>platoonID:EntityID of the rescue agent</li>
	 * </ul>
	 * 
	 * @param time
	 *            ステップ数<br>
	 *            step num
	 * @param platoonID
	 *            救助隊のEntityID<br>
	 *            EntityID of the rescue agent.
	 */
	public DoneReportMessage(int time, EntityID platoonID) {
		super(BaseMessageType.DONE, time, platoonID);
	}

	/**
	 * コンバート時システムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public DoneReportMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.DONE, bitList, offset, bitSizeMap);
	}

}
