package com.work.fyc.MyWork.mvp;

import com.work.fyc.MyWork.entity.MainDataEntity;
import com.work.fyc.MyWork.entity.ResultEntity;
import com.work.fyc.MyWork.net.HttpMethods;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

/**
 * 可以把一些逻辑计算，网络请求放在这里。避免Activity的代码太多
 * 这是最简单的MVP架构。现在市场上用的应该最多
 * <p>
 * 说白了。就是把 Activity的代码 复制到这里做。做完了，使用interface调用Activity的后续代码
 */
public class MainPresenter {
    private IMainView view;

    public MainPresenter(IMainView view) {
        this.view = view;
    }

    //Activity 销毁的时候，销毁Presenter
    public void onDestroy() {
        view = null;
    }

    /**
     * 网络请求，再通过 interface IMainView 返回结果
     * 等效于
     * getCard(http://2579f0157d.wicp.vip:80/MyForum1/main)
     * 一般情况很少使用 Thread 和 Handler 。 因为 Thread 和 Handler 容易引起内存问题。
     * 以后成为大神了就可以用Thread和Handler了。
     */
    public void getMainCard(String nameId, final String section) {

        //这是上传参数的map。用 map 比 jsonObject 方便，不需要处理异常
        Map<String, String> map = new HashMap<>();
        map.put("nameId", nameId);
        map.put("section", section);

        /*
         *网络请求。这个是写好的工具类，方法照抄直接用即可。
         *这里默认 异步线程请求网络 。
         * onCompleted，onError，onNext 会走主线程。
         * 线程切换已经帮你完成了。可以不用 Thread 和 Handler
         * */
        HttpMethods.getInstance().requestSubscribe(
                HttpMethods.getInstance().getApiService().getMainList(map),
                new Subscriber<MainDataEntity>() {
                    @Override
                    public void onCompleted() {
                        //onNext 或者 onError 走完之后走这里
                    }

                    @Override
                    public void onError(Throwable e) {
                        //如果网络报错，或者 onNext里代码报错，走这里
                        if (view != null)//必须判断空
                            view.failToast(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(MainDataEntity resultEntity) {
                        if (view != null && resultEntity != null) {//必须判断空
                            view.returnSectionCards(resultEntity.section, resultEntity.card, section);
                        }
                    }
                }
        );
    }

    public void getSelfCard(String nameId, final String section) {

        //这是上传参数的map。用 map 比 jsonObject 方便，不需要处理异常
        Map<String, String> map = new HashMap<>();
        map.put("nameId", nameId);
        map.put("section", section);

        /*
         *网络请求。这个是写好的工具类，方法照抄直接用即可。
         *这里默认 异步线程请求网络 。
         * onCompleted，onError，onNext 会走主线程。
         * 线程切换已经帮你完成了。可以不用 Thread 和 Handler
         * */
        HttpMethods.getInstance().requestSubscribe(
                HttpMethods.getInstance().getApiService().getSelfCard(map),
                new Subscriber<MainDataEntity>() {
                    @Override
                    public void onCompleted() {
                        //onNext 或者 onError 走完之后走这里
                    }

                    @Override
                    public void onError(Throwable e) {
                        //如果网络报错，或者 onNext里代码报错，走这里
                        if (view != null)//必须判断空
                            view.failToast(e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(MainDataEntity resultEntity) {
                        if (view != null && resultEntity != null) {//必须判断空
                            view.returnSectionCards(resultEntity.section, resultEntity.card, section);
                        }
                    }
                }
        );
    }

    /*删帖的API 网络请求 。 具体可以参考 getMainCard 的说明*/
    public void deletePost(String id, final int position) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        HttpMethods.getInstance().requestSubscribe(
                HttpMethods.getInstance().getApiService().deletePost(map),
                new Subscriber<ResultEntity>() {
                    @Override
                    public void onCompleted() {
                        //onNext 或者 onError 走完之后走这里
                    }

                    @Override
                    public void onError(Throwable e) {
                        //如果网络报错，或者 onNext里代码报错，走这里
                        if (view != null)//必须判断空
                            view.failToast("删除失败");
                    }

                    @Override
                    public void onNext(ResultEntity resultEntity) {
                        if (view != null && resultEntity != null) {//必须判断空
                            if ("success".equals(resultEntity.Result))
                                view.deleteSuccess(position);
                            else if ("error".equals(resultEntity.Result))
                                view.failToast("删除失败");
                        }
                    }
                }
        );
    }

    /*置顶的API 网络请求 。 具体可以参考 getMainCard 的说明*/
    public void keepTop(String id, final int position) {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        HttpMethods.getInstance().requestSubscribe(
                HttpMethods.getInstance().getApiService().keepTopPost(map),
                new Subscriber<ResultEntity>() {
                    @Override
                    public void onCompleted() {
                        //onNext 或者 onError 走完之后走这里
                    }

                    @Override
                    public void onError(Throwable e) {
                        //如果网络报错，或者 onNext里代码报错，走这里
                        if (view != null)//必须判断空
                            view.failToast("置顶失败");
                    }

                    @Override
                    public void onNext(ResultEntity resultEntity) {
                        if (view != null && resultEntity != null) {//必须判断空
                            if ("success".equals(resultEntity.Result))
                                view.topSuccess(position);
                            else if ("error".equals(resultEntity.Result))
                                view.failToast("置顶失败");
                        }
                    }
                }
        );
    }
}
