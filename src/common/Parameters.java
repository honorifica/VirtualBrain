package common;

/**
 * ���ڴ�ź������õ���һЩͨ�ò���������ں����Χ�ĸ�
 * @author �����
 *
 */
public class Parameters {
	public static int showSize = 450;//���ڳ��ֽ���������С
	public static int sigGridSize = 45;//���ڶ�ȡ�����ʱ�򵥸���������Ĵ�С
	public static int gridMapSize = showSize/sigGridSize;//���ڴ���и�֮��Ĵ��������С
	public static float invalidRatio = 0.6f;//����ָʾ��ͼƬ�����ȡ��ʱ��,һ�����������ڽϰ׻��߽����������������ռ��֮��᲻���м���
	public static int invalidAmount = ratioToAmount();
	public static int corAmount = 250;//����ָʾ������¼��������������
	public static float shapeAlphaComposite = 0.3f;//�ڳ��ֽ����ʱ���������ͼ�ε�͸����
	public static float gridAlphaComposite = 0.3f;//�ڳ��ֽ����ʱ��������������͸����
	public static int shapeSize = 10;//����ָʾ�ڳ��ֻ滭�����ʱ��ͼ�εĴ�С
	private static int ratioToAmount() {
		float temp = sigGridSize*sigGridSize*invalidRatio;
		return (int)temp;
	}
}
