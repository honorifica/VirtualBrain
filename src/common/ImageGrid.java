package common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * 用于对图片进行45像素为方格的10*10网格化处理，得到需要的数据
 * @author 姜云骞
 *
 */
public class ImageGrid {
	/*
	 * 根据RGB颜色空间范围进行划分
	 * 暗块和两块全部滤掉，直接靠正方体分块
	 */
	int BLACK_RANGE = 20;
	private int[][] gridMap = new int[Parameters.gridMapSize][Parameters.gridMapSize];//用于最终存储当前的区域块被当作什么色块。0：无效/4：红/3：橙/2：黄/1：绿
	enum ColorRange{RED,ORANGE,YELLOW,GREEN}//不同的范围用于指示后续的坐标生成个数
	private Vector<Cordinate> redCorVec;//存放红色区块坐标
	private Vector<Cordinate> orangeCorVec;//存放橙色区块坐标
	private Vector<Cordinate> yellowCorVec;//存放黄色区块坐标
	private Vector<Cordinate> greenCorVec;//存放绿色区块坐标
	public ImageGrid() {}
	/**
	 * 构造函数
	 * @param imagePath 图片的路径,需要为绝对路径
	 */
	public ImageGrid(String imagePath) {
		redCorVec = new Vector<>();
		orangeCorVec = new Vector<>();
		yellowCorVec = new Vector<>();
		greenCorVec = new Vector<>();
		int[] rgb = new int[3];
        File file = new File(imagePath);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        ColorRange colorRange = null;
        for(int gX = 0;gX<Parameters.gridMapSize;gX++) {
        	for(int gY=0;gY<Parameters.gridMapSize;gY++) {
        		int redAmount = 0;
        		int orangeAmount = 0;
        		int yellowAmount = 0;
        		int greenAmount = 0;
        		int invalidAmount = 0;
        		for (int i = gY*Parameters.sigGridSize; i < gY*Parameters.sigGridSize + Parameters.sigGridSize; i++) {
        	          for (int j = gX*Parameters.sigGridSize; j < gX*Parameters.sigGridSize + Parameters.sigGridSize; j++) {
        	              int pixel = bufferedImage.getRGB(i, j); // 下面三行代码将一个数字转换为RGB数字
        	              rgb[0] = (pixel & 0xff0000) >> 16;
        	              rgb[1] = (pixel & 0xff00) >> 8;
        	              rgb[2] = (pixel & 0xff);
        	              if(isValid(rgb)) {
        	            	  colorRange = getRange(rgb);
        	            	  if(colorRange == ColorRange.RED) {
        	            		  redAmount++;
        	            	  } else if(colorRange == ColorRange.ORANGE) {
        	            		  orangeAmount++;
        	            	  } else if(colorRange == ColorRange.YELLOW) {
        	            		  yellowAmount++;
        	            	  } else if(colorRange == ColorRange.GREEN) {
        	            		  greenAmount++;
        	            	  }
        	              } else {
        	            	  invalidAmount++;
        	              }
        	          }
        		}
        		if(invalidAmount > Parameters.invalidAmount) gridMap[gX][gY] = 0;
        		else {
        			gridMap[gX][gY] = calGridType(redAmount,
        					orangeAmount,yellowAmount,greenAmount);
        		}
        	}
        }
        for(int i=0;i<Parameters.gridMapSize;i++) {
        	for(int j=0;j<Parameters.gridMapSize;j++) {
        		int corAmount = gridMap[i][j];
        		for(int k=0;k<corAmount;k++) {
        			Cordinate cordinate = new Cordinate();
        			Random random = new Random();
        			cordinate.setCordinate(random.nextInt(Parameters.sigGridSize)+j*Parameters.sigGridSize, random.nextInt(Parameters.sigGridSize)+i*Parameters.sigGridSize);
        			if(corAmount==1) {
        				greenCorVec.add(cordinate);
        			} else if(corAmount == 2) {
        				yellowCorVec.add(cordinate);
        			} else if(corAmount == 3) {
        				orangeCorVec.add(cordinate);
        			} else if(corAmount==4) {
        				redCorVec.add(cordinate);
        			}
        		}
        	}
        }
	}
    /**
     * 通过区域统计量的大小来决定该区域的颜色偏向哪边
     * @param redAmount 红色像素数量
     * @param orangeAmount 橘色像素数量
     * @param yellowAmount 黄色像素数量
     * @param greenAmount 绿色像素数量
     * @return 返回最终该区域的颜色对应值。越深值越大。
     */
	private int calGridType(int redAmount, int orangeAmount, int yellowAmount,int greenAmount) {
		int biggerOne=0;
		int gridType=-1;
		if(redAmount > biggerOne) {
			biggerOne = redAmount;
			gridType = 4;
		}
		if(orangeAmount > biggerOne) {
			biggerOne = orangeAmount;
			gridType = 3;
		}
		if(yellowAmount > biggerOne) {
			biggerOne = yellowAmount;
			gridType = 2;
		}
		if(greenAmount > biggerOne) {
			biggerOne = greenAmount;
			gridType = 1;
		}
		return gridType;
	}
	/**
	 * 用于判断当前的rgb左边是不是色彩较为鲜明的，可用于后续处理的有效数据
	 * @param rgb 三个元素的数组，用于指示r,g,b的值
	 * @return 有效时返回为真
	 */
	private boolean isValid(int[] rgb) {
		if(rgb[0] > BLACK_RANGE && rgb[1] > BLACK_RANGE && rgb[2] > BLACK_RANGE &&
				rgb[0] < 230 && rgb[1] < 230 && rgb[2] < 230) return true;
		else return false;
	}
	/**
	 * 用于判断当前的RGB色彩是在谁的范围之内。
	 * @param rgb 用于判断的RGB元素数组
	 * @return 返回判断的种类
	 */
	private ColorRange getRange(int[] rgb) {
		ColorRange colorRange = null;
		if(rgb[0] >= BLACK_RANGE && rgb[1] < 115 && rgb[0]>=rgb[1]) colorRange = ColorRange.RED;
		else if(rgb[0] >= rgb[1] && rgb[1] >= 115 && rgb[1] < 180) colorRange = ColorRange.ORANGE;
		else if(rgb[0] >= 200 && rgb[1]>= 180) colorRange = ColorRange.YELLOW;
		else if(rgb[0] < 200 && rgb[1] >= BLACK_RANGE) colorRange = ColorRange.GREEN;
		return colorRange;
	}

	/**
	 * 用于将图片中计算出来的中心坐标按照需要进行再处理
	 * 遵循从高到低的存入顺序
	 * 当 当前数组的数量大过了剩余所需，则随机选取足够的数量
	 * 反之全部录入
	 * @param size 最终需要的数量
	 * @return requiredCorVec 处理完的最终坐标数组
	 */
	public Vector<Cordinate> getNormalizedColorVec(int size) {
		if(size>Parameters.corAmount) return null;
		Vector<Cordinate> requiredCorVec=new Vector<>();
		if(size >= redCorVec.size()) {
			for(int i=0;i<redCorVec.size();i++) {
				requiredCorVec.add(redCorVec.get(i));
			}
			size -= redCorVec.size();
			if(size>=orangeCorVec.size()) {
				for(int i=0;i<orangeCorVec.size();i++) {
					requiredCorVec.add(orangeCorVec.get(i));
				}
				size -= orangeCorVec.size();
				if(size>yellowCorVec.size()) {
					for(int i=0;i<yellowCorVec.size();i++) {
						requiredCorVec.add(yellowCorVec.get(i));
					}
					size-=yellowCorVec.size();
					if(size>greenCorVec.size()) {
						for(int i=0;i<greenCorVec.size();i++) {
							requiredCorVec.add(greenCorVec.get(i));
						}
					} else {
						for(int i=0;i<size;i++) {
							Random random = new Random();
							requiredCorVec.add(greenCorVec.get(random.nextInt(greenCorVec.size())));
						}
					}
				} else{
					for(int i=0;i<size;i++) {
						Random random = new Random();
						requiredCorVec.add(yellowCorVec.get(random.nextInt(yellowCorVec.size())));
					}
				}
			} else {
				for(int i=0;i<size;i++) {
					Random random = new Random();
					requiredCorVec.add(orangeCorVec.get(random.nextInt(orangeCorVec.size())));
				}
			}
		} else{
			for(int i=0;i<size;i++) {
				Random random = new Random();
				requiredCorVec.add(redCorVec.get(random.nextInt(redCorVec.size())));
			}
		}
		return requiredCorVec;
	}
	public int[][] getGridMap() {
		return gridMap;
	}
	public void setGridMap(int[][] gridMap) {
		this.gridMap = gridMap;
	}
	public void printGridMap() {
		int total=0;

		for(int i=0;i<Parameters.gridMapSize;i++) {
			for(int j = 0;j<Parameters.gridMapSize;j++) {
				System.out.print(gridMap[i][j] + " ");
				total+=gridMap[i][j];
			}
			System.out.println();
		}
		System.out.println("in total: " + total);
		System.out.println("red vec size: " + redCorVec.size());
		System.out.println("orange vec size: " + orangeCorVec.size());
		System.out.println("yellow vec size: " + yellowCorVec.size());
		System.out.println("green vec size " + greenCorVec.size());
	}
}
