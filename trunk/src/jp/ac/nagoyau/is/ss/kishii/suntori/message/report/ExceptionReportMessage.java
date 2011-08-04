package jp.ac.nagoyau.is.ss.kishii.suntori.message.report;

import java.util.EnumMap;
import java.util.List;

import jp.ac.nagoyau.is.ss.kishii.suntori.message.BaseMessageType;
import jp.ac.nagoyau.is.ss.kishii.suntori.message.data.DataType;
import rescuecore2.worldmodel.EntityID;

/**
 * 割り当てられたタスクが実行不可能であることを表すメッセージクラスです．<br>
 * This class is the Message Class that report the given task is not able to
 * complete at the moment.
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
	 * <h2>Constructor</h2> Create the message that report the given task is
	 * uncompletable.<br>
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
	public ExceptionReportMessage(int time, EntityID platoonID) {
		super(BaseMessageType.EXCEPTION, time, platoonID);
	}

	/**
	 * コンバート時システムが使用するコンストラクタ <br>
	 * The method that the library use to convert the message.
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
