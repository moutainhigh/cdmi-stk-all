package pw.cdmi.starlink.orbit.updater.services;

import java.util.Date;
import java.util.List;

import pw.cdmi.starlink.orbit.updater.modules.entities.HistoryOrbit;
import pw.cdmi.starlink.orbit.updater.modules.entities.LatestOrbit;

public interface OrbitUpdaterService {

	/**
	 * 从远程HTTP 文件中读取最新的卫星两行数据
	 * 
	 * @param httpfile 远程文件
	 */
	public void readTowLineFileFromRemote(String httpfile);

	/**
	 * 向数据库中尝试写入卫星两行数据
	 * 
	 * @param name    卫星两行数据中的卫星名称
	 * @param line1     卫星两行数据中的首行数据
	 * @param line2     卫星两行数据中的第二行数据
	 */
	public void saveNewTowLineTodb(String name, String line1, String line2);
	
	/**
	 * 向数据库中写入卫星最新的两行文件
	 * 
	 * @param satelId 系统分配给卫星的卫星编号
	 * @param name    卫星两行数据中的卫星名称
	 * @param line1     卫星两行数据中的首行数据
	 * @param line2     卫星两行数据中的第二行数据
	 */
	public void saveNewTowLineTodb(String satelId, String name, String line1, String line2);
	
	/**
	 * 获得指定卫星当前最新的瞬根数据
	 * 
	 * @param satelId 系统分配给卫星的卫星编号
	 * @return
	 */
	public LatestOrbit getLatestOrbit(String satelId);
	
	/**
	 * 获得系统你内的所有卫星列表最新轨迹，卫星不重复
	 * 
	 * @return
	 */
	public List<LatestOrbit> listLatestOrbit();
	
	/**
	 * 从数据库中获取指定卫星的历史瞬根数据
	 * @param satelId      系统分配给卫星的系统编号
	 * @param startTime    要查询的瞬根数据的最早时间
	 * @param endTime      要查询的瞬根数据的最晚时间
	 * @param cursor       结果指针，从那条记录开始
	 * @param maxsize      返回结果的最大记录数
	 * @return 
	 */
	public List<HistoryOrbit> listHistoryOrbit(String satelId, Date startTime, Date endTime, int cursor, int maxsize);
	
	/**
	 * 从数据库中读取卫星指定的历史瞬根信息
	 * 
	 * @param twolineId
	 * @return 长度为3，数组中的第一个数字为两行数据中的卫星名称，第二个数据是两行数据首行数据，第三个数据为卫星两行数据中的第二行数据
	 */
	public HistoryOrbit getOrbit(String twolineId);
}
