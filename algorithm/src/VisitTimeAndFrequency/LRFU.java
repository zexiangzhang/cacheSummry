package VisitTimeAndFrequency;

import java.util.Date;
import java.util.Objects;

public class LRFU {

    private final static Integer CACHE_SIZE = 5;

    private CacheObject[] cachedObjects = new CacheObject[CACHE_SIZE];

    public void put(String key,Integer value){
        CacheObject newCacheObject = new CacheObject(key, value);
        Integer cachePosition = getCacheIndex(key);
        if(isOccupied(cachePosition)){
            evictAndInsertItem(cachePosition, newCacheObject);
        }else
            cachedObjects[cachePosition] = newCacheObject;

    }

    private void evictAndInsertItem(Integer cachePosition,CacheObject newCacheObject){
        CacheObject cacheObject = getCachedObject(cachePosition);
        if (Objects.nonNull(cacheObject)) {
            Integer nextBestPosition = findNextBestPosition();
            Integer headIndex = getHeadIndex(cacheObject, cachePosition);

            if(headIndex.equals(cachePosition)){
                insertAtIndex(newCacheObject, nextBestPosition, headIndex);

            }else{
                CacheObject bestPositionItem = getCachedObject(nextBestPosition);
                cachePosition = evictItem(bestPositionItem,cachePosition);
                CacheObject oldObject = getCachedObject(cachePosition);
                if (Objects.nonNull(oldObject)) {
                    cachedObjects[cachePosition] = newCacheObject;
                    cachedObjects[nextBestPosition] = oldObject;
                    connectNodes(oldObject.getPrevIndex(), nextBestPosition);
                    connectNodes(nextBestPosition, oldObject.getNextIndex());
                    addCurrentItemToTail(cachePosition, headIndex);
                }
            }
        }

    }


    private void insertAtIndex(CacheObject cacheObject,Integer insertIndex,Integer headIndex){
        if(!isOccupied(insertIndex)){
            cachedObjects[insertIndex] = cacheObject;
            addCurrentItemToTail(insertIndex, headIndex);

        }else{
            CacheObject bestPositionItem = getCachedObject(insertIndex);
            cachedObjects[insertIndex] = cacheObject;
            if(bestPositionItem!=null){
                connectNodes(bestPositionItem.getPrevIndex(), insertIndex);
                connectNodes(insertIndex, bestPositionItem.getNextIndex());
            }
        }

    }



    private Integer findNextBestPosition(){
        CacheObject matchingCachedObject = cachedObjects[0];
        int matchingIndex = 0;
        for(int i=0;i<CACHE_SIZE;i++){
            if(!isOccupied(i))
                return i;
            CacheObject cachedObject = cachedObjects[i];
            if(cachedObject.isBetterSuitedForEviction(matchingCachedObject)){
                matchingCachedObject = cachedObject;
                matchingIndex = i;
            }

        }
        return matchingIndex;
    }


    private int evictItem(CacheObject cacheObject,int oldIndex){
        if(cacheObject == null)
            return oldIndex;
        CacheObject prevItem = getPreviousItem(cacheObject);
        int nextIndex = cacheObject.getNextIndex();
        CacheObject nextItem = getNextItem(cacheObject);
        if(prevItem!=null && nextItem!=null){
            prevItem.setNextIndex(cacheObject.getNextIndex());
            nextItem.setPrevIndex(cacheObject.getPrevIndex());
        }
        else if(prevItem!=null){
            prevItem.clearNextItem();
        }else if(nextItem!=null){
            Integer tailIndex = getTailIndex(cacheObject, nextItem.getPrevIndex());
            CacheObject tailObject = cachedObjects[tailIndex];
            cachedObjects[nextItem.getPrevIndex()]= tailObject;

            tailObject.clearPrevItem();
            connectNodes(nextItem.getPrevIndex(), nextIndex);
            oldIndex = tailIndex;



        }
        return oldIndex;
    }

    private void connectNodes(int currentNodeIndex,int nextNodeIndex){
        CacheObject current = getCachedObject(currentNodeIndex);
        CacheObject nextNode = getCachedObject(nextNodeIndex);
        if(current!=null)
            current.setNextIndex(nextNodeIndex);

        if(nextNode!=null)
            nextNode.setPrevIndex(currentNodeIndex);
    }

    private void addCurrentItemToTail(int currentIndex,int headArrayPosition){
        CacheObject head = getCachedObject(headArrayPosition);
        if (Objects.nonNull(head)) {
            Integer tailIndex = getTailIndex(head, headArrayPosition);
            connectNodes(tailIndex, currentIndex);
        }
    }

    private Integer getHeadIndex(CacheObject cacheObject,Integer index){

        while(!cacheObject.hasNoPreviousItems()){
            index = cacheObject.getPrevIndex();
            cacheObject = getPreviousItem(cacheObject);
        }
        return index;
    }

    private Integer getTailIndex(CacheObject cacheObject,Integer index){
        while(!cacheObject.hasNoNextItems()){
            index = cacheObject.getNextIndex();
            cacheObject = getNextItem(cacheObject);
        }
        return index;
    }

    private boolean isOccupied(Integer index){
        return cachedObjects[index]!=null;
    }
    public Integer get(String key){
        Integer cachePosition = getCacheIndex(key);
        CacheObject cachedObject = getCachedObject(cachePosition);
        if(cachedObject != null){
            CacheObject matchedObject = iterateLLToFindMatchingKey(key, cachedObject);
            if(matchedObject!=null){
                matchedObject.advanceFrequency();
                matchedObject.updateLastAccessedTime();
                return matchedObject.getValue();
            }
        }
        return -1;
    }

    private CacheObject iterateLLToFindMatchingKey(String key,CacheObject head){
        CacheObject cachedObject = head;
        while(true){
            if(cachedObject.key.equals(key)){
                return cachedObject;
            }
            if(cachedObject.hasNoNextItems())
                break;
            cachedObject = getNextItem(cachedObject);
        }
        return null;
    }
    private CacheObject getCachedObject(Integer index){
        if(index<0)
            return null;
        else
            return cachedObjects[index];

    }
    private CacheObject getNextItem(CacheObject firstItem){

        return getCachedObject(firstItem.nextIndex);
    }
    private CacheObject getPreviousItem(CacheObject firstItem){
        return getCachedObject(firstItem.prevIndex);
    }
    private Integer getCacheIndex(String key){
        Integer hash = key.hashCode();
        return hash%CACHE_SIZE;
    }


    static class CacheObject{
        private String key;
        private Integer value,frequency,prevIndex,nextIndex;
        private Date lastAccessedDate;

        CacheObject(String key,Integer value){
            lastAccessedDate = new Date();
            prevIndex = -1;
            frequency = 1;
            this.key = key;
            this.value = value;
            nextIndex = -1;
        }

        public void advanceFrequency(){

            frequency = frequency+1;
        }
        public void updateLastAccessedTime(){
            lastAccessedDate = new Date();
        }
        public boolean hasNoNextItems(){
            return nextIndex==-1;
        }

        public boolean hasNoPreviousItems(){
            return prevIndex==-1;
        }



        public boolean isBetterSuitedForEviction(CacheObject previousBest){
            if(this.frequency<previousBest.frequency){
                return true;
            }else if(this.frequency == previousBest.frequency){
                return this.lastAccessedDate.compareTo(previousBest.getLastAccessedDate())<0;
            }else
                return false;
        }


        public void clearNextItem(){
            this.nextIndex =-1;
        }

        public void clearPrevItem(){
            this.prevIndex =-1;
        }
        public Integer getPrevIndex() {
            return prevIndex;
        }
        public void setPrevIndex(Integer prevIndex) {
            this.prevIndex = prevIndex;
        }
        public Integer getNextIndex() {
            return nextIndex;
        }
        public void setNextIndex(Integer nextIndex) {
            this.nextIndex = nextIndex;
        }
        public String getKey() {
            return key;
        }
        public void setKey(String key) {
            this.key = key;
        }
        public Integer getValue() {
            return value;
        }
        public void setValue(Integer value) {
            this.value = value;
        }
        public Integer getFrequency() {
            return frequency;
        }
        public void setFrequency(Integer frequency) {
            this.frequency = frequency;
        }
        public Date getLastAccessedDate() {
            return lastAccessedDate;
        }
        public void setLastAccessedDate(Date lastAccessedDate) {
            this.lastAccessedDate = lastAccessedDate;
        }

        @Override
        public String toString() {
            return "CacheObject [key=" + key + ", value=" + value + ", frequency=" + frequency + ", prevIndex="
                    + prevIndex + ", nextIndex=" + nextIndex + ", lastAccessedDate=" + lastAccessedDate + "]";
        }

    }

}
