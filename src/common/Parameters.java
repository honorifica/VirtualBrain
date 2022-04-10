package common;

/**
 * 用于存放后续会用到的一些通用参数。免得在后面大范围的改
 * @author 姜云骞
 *
 */
public class Parameters {
	public static int showSize = 450;//用于呈现结果的区域大小
	public static int sigGridSize = 45;//用于读取结果的时候单个处理区域的大小
	public static int gridMapSize = showSize/sigGridSize;//用于存放切割之后的处理数组大小
	public static float invalidRatio = 0.6f;//用于指示在图片结果读取的时候,一个处理区域内较白或者较深的数量超过多少占比之后会不进行计量
	public static int invalidAmount = ratioToAmount();
	public static int corAmount = 250;//用于指示单条记录当中最大的坐标数
	public static float shapeAlphaComposite = 0.3f;//在呈现结果的时候的所绘制图形的透明度
	public static float gridAlphaComposite = 0.3f;//在呈现结果的时候的所绘制网格的透明度
	public static int shapeSize = 10;//用于指示在呈现绘画结果的时候图形的大小
	private static int ratioToAmount() {
		float temp = sigGridSize*sigGridSize*invalidRatio;
		return (int)temp;
	}
}
