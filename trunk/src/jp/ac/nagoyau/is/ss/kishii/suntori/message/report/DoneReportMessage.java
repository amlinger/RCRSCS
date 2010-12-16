package jp.ac.nagoyau.is.ss.kishii.suntori.message.report;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import rescuecore2.worldmodel.EntityID;

/**
 * タスクが完了したことを報告するためのメッセージクラスです．
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
	 * 
	 * @param time
	 *            ステップ数
	 * @param platoonID
	 *            救助隊のEntityID
	 */
	public DoneReportMessage(int time, EntityID platoonID) {
		super(BaseMessageType.DONE, time, platoonID);
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
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
