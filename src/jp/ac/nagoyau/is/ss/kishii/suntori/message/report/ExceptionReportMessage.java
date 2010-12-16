package jp.ac.nagoyau.is.ss.kishii.suntori.message.report;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import rescuecore2.worldmodel.EntityID;

/**
 * 割り当てられたタスクが実行不可能であることを表すメッセージクラスです．
 * 
 * @author takefumi
 * 
 */
public class ExceptionReportMessage extends ReportMessage {
	/**
	 * コンストラクタ<br>
	 * 割り当てタスク実行不能報告メッセージを生成します．<br>
	 * 含まれる情報は以下のものです．
	 * <ul>
	 * <li>time:情報生成時に設定した(情報が作成されたと考えられる)時間</li>
	 * <li>platoonID:救助隊のEntityID</li>
	 * </ul>
	 * 
	 * @param time
	 * @param platoonID
	 */
	public ExceptionReportMessage(int time, EntityID platoonID) {
		super(BaseMessageType.EXCEPTION, time, platoonID);
	}

	/**
	 * コンバート時にシステムが使用するコンストラクタ
	 * 
	 * @param bitList
	 * @param offset
	 * @param bitSizeMap
	 */
	public ExceptionReportMessage(List<Integer> bitList, int offset,
			EnumMap<DataType, Integer> bitSizeMap) {
		super(BaseMessageType.EXCEPTION, bitList, offset, bitSizeMap);
	}

}
