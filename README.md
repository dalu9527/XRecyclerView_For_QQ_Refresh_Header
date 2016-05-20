# 仿IOS版QQ下拉刷新布局

## 介绍 ##

本项目是根据 IPHONE 版 QQ 的下拉刷新布局，基于 XRecyclerView 实现的 Android版 QQ 下拉刷新布局。项目[地址](https://github.com/dalu9527/XRecyclerView_For_QQ_Refresh_Header)，欢迎使用，PR，喜欢的话请 star 一下。效果如下：（录屏软件比较渣，大家可以自我运行观察一下效果）

![](http://i.imgur.com/dFQLAJg.gif)

![效果图](http://www.asqql.com/upfile/tmp/20160519/20160519213455_40775.gif)

QQ 的下拉刷新样式（原来是大 Twitter 的[专利](https://isux.tencent.com/pull-down-to-reflesh.html)）

![](https://pic3.zhimg.com/158ef734fbc2bcf56806892d92269482_b.jpg)

![](https://isux.tencent.com/wp-content/uploads/2013/06/20130606165611996.png)

![](https://isux.tencent.com/wp-content/uploads/2013/06/20130606165611996.png)

## 缘由 ##

实现这个的原因也没啥，就是好奇（其实最近在看 贝赛尔曲线）。大家可以拿出自己的苹果手机（非常抱歉，Android 版的 QQ 下拉刷新的实现和苹果版不一样，就忽略 Android 了。如果没有苹果手机，我也没有办法。。。），下来刷新看一下 QQ 下拉刷新时的样貌。

## How ##

本项目的实现是基于以下博客的帮助

[三次贝塞尔曲线练习之弹性的圆](http://www.jianshu.com/p/791d3a791ec2)

[Path之贝塞尔曲线](https://github.com/GcsSloop/AndroidNote/blob/master/CustomView/Advance/%5B6%5DPath_Bezier.md)：这个讲的比较全面，可以运行一下样例，对学习贝塞尔曲线有帮助

[Android 贝塞尔曲线的魅力](http://blog.csdn.net/qq_21430549/article/details/50040241)

[BezierDemo](https://github.com/chenupt/BezierDemo)：这个是上面博客提到的一个项目，也正是这个项目，才发现，QQ 的实现可能和贝塞尔曲线有关系（个人猜测而已，毕竟我就是利用贝塞尔曲线完成的）

其中，我在学习上面的Demo时，通过自己稍微的改动，发下了如下的效果

![](http://i.imgur.com/dFQLAJg.gif)

大家再对比一下 QQ 的下拉刷新，发现是不是很相似，既然发现了这个，那么接下来就是考虑怎么移植到下拉刷新的头部了（这里用的是我自己写的 XRecyclerView 控件，并稍微对齐改造了一下）

## 实现 ##

实现上面的效果需要的知识储备有

- [贝赛尔曲线](https://zh.wikipedia.org/wiki/%E8%B2%9D%E8%8C%B2%E6%9B%B2%E7%B7%9A)：这次的实现就是和它有关系
- [XRecyclerView](https://github.com/dalu9527/XRecyclerView)：一款自己写的，为 RecyclerView 添加下拉刷新和上拉加载的控件（欢迎大家使用，PR，喜欢的可以 star 一下）

### 第一步 ###

如何利用贝塞尔曲线绘制出上图显示的效果呢？核心代码如下：

	float offsetX = mRadius;

    float x1 = mStartX - offsetX;
    float y1 = mStartY;
    float x4 = mStartX + offsetX;
    float y4 = mStartY;

    float x2 = mStartX - offsetX;
    float y2 = y;
    float x3 = mStartX + offsetX;
    float y3 = y;

    mPath.reset();
    mPath.moveTo(x1, y1);
    mPath.quadTo(mStartX, mStartY, x2 + mStep, y2);
    mPath.lineTo(x3 - mStep, y3);
    mPath.quadTo(mStartX, mStartY, x4, y4);
    mPath.lineTo(x1, y1);

代码很少，其实就是找到贝塞尔曲线关键的两点：控制点和坐标点

### 第二步 ###

如何将布局放到 XRecyclerView 中呢？

XRecyclerview 添加头部布局很简单，下面代码就可以实现

 	mHeaderView = LayoutInflater.from(this).inflate(R.layout.qq_header_view, null);
	mXRecyclerView.addHeaderView(mHeaderView, 50);

但是，有一个问题，QQ 的下拉刷新，会随着向下拉的距离增大而变化，而 XRecyclerView 中并没有实现这样的接口，所以，需要对 XRecyclerView 进行改造，为 XRecyclerView 添加两个接口回调，分别为

	/** 用于监控是否刷新完成，用于隐藏布局*/
	public interface OnRefreshCompleteListenter {
	    void refreshComplete();
	}
	/** 用于记录下拉头部的距离，方便形成圆抽长的效果*/
	public interface OnRefreshDistanceListener {
	    void refreshDistance(int dy);
	}

有了上面两个接口，接下来就好办了

### 第三步 ###

根据接口实现功能

	mXRecyclerView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                qqRefreshView.start();
            }
    });
    mXRecyclerView.setOnRefreshDistanceListener(new OnRefreshDistanceListener() {
            @Override
            public void refreshDistance(int dy) {
                qqRefreshView.setVY(dy);
            }
    });
    mXRecyclerView.setOnRefreshCompleteListenter(new OnRefreshCompleteListenter() {
            @Override
            public void refreshComplete() {
                qqRefreshView.stop();
            }
    });

其中， `qqRefreshView.start()` 和 `qqRefreshView.stop()` 用于实现 QQ 水滴断裂后的圆盘旋转的效果

`qqRefreshView.setVY(dy)` 是将下滑的距离传入布局中，方便让圆逐渐抽长至断裂

至此，就可以实现类似 QQ 下拉刷新的布局和操作。

## 不足 ##

虽然实现了大概的样貌，但是还有很多细节没有完善好

- 下拉刷新的圆抽长的过度效果和 QQ 其实是有差距的，主要还是贝塞尔曲线公式没有调整到完美
- QQ 的下拉刷新是等圆全部出来后，继续往下拉才会变的修长；而目前实现的是，下拉的时候就开始便修长，这点在细节体验上会差一些

## 总结 ##

虽然效果差强人意，但是在实现过程中，还是有很多的收获，同时也发现了自己编写的 XRecyclerView 的一些不足，学习永无止境，For Google  & Android，I Do。



