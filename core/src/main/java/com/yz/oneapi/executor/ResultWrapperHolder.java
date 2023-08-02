package com.yz.oneapi.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultWrapperHolder {

    private static final ThreadLocal<ResultWrapperHolder> resultWrapperThreadLocal = new ThreadLocal<>();

    private ResultWrapper resultWrapper;

    private Map<String, List<Object>> tableData;

    //与父节点的关联字段，父子关联目前只允许一个
    private Map<String, JoinOn> joinParent;

    public ResultWrapperHolder(ResultWrapper resultWrapper) {
        this.resultWrapper = resultWrapper;
        tableData = new HashMap<>();
        joinParent = new HashMap<>();
    }


    public static ResultWrapperHolder getResult() {
        return resultWrapperThreadLocal.get();
    }
    public static void remove(){
        resultWrapperThreadLocal.remove();
    }
    public static void set(ResultWrapperHolder resultWrapperHolder) {
        resultWrapperThreadLocal.set(resultWrapperHolder);
    }

    public JoinOn getJoinParent(String model){
        return joinParent.get(model);
    }

    public void addJoin(String model, JoinOn join){
        joinParent.put(model, join);
    }

    public void addData(String model, List<Object> data) {
        tableData.put(model, data);
    }

    public List<Object> getData(String model){
        return tableData.get(model);
    }


    public static class JoinOn {
        private String leftProperty;
        private String rightProperty;

        public JoinOn(String leftProperty, String rightProperty) {
            this.leftProperty = leftProperty;
            this.rightProperty = rightProperty;
        }

        public String getLeftProperty() {
            return leftProperty;
        }

        public void setLeftProperty(String leftProperty) {
            this.leftProperty = leftProperty;
        }

        public String getRightProperty() {
            return rightProperty;
        }

        public void setRightProperty(String rightProperty) {
            this.rightProperty = rightProperty;
        }
    }
}
