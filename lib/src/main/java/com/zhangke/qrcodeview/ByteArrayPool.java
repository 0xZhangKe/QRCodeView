package com.zhangke.qrcodeview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * cache byte[] object;
 * Created by ZhangKe on 2017/11/8.
 */

public class ByteArrayPool {

    private static ByteArrayPool byteArrayPool = new ByteArrayPool(5);

    private ByteArrayPool(int sizeLimit) {
        mSizeLimit = sizeLimit;
    }

    public static ByteArrayPool getInstance(){
        return byteArrayPool;
    }

    private final List<byte[]> mBuffersByLastUse = new LinkedList<byte[]>();
    private final List<byte[]> mBuffersBySize = new ArrayList<byte[]>(64);

    private int mCurrentSize = 0;

    private final int mSizeLimit;

    protected static final Comparator<byte[]> BUF_COMPARATOR = new Comparator<byte[]>() {
        @Override
        public int compare(byte[] lhs, byte[] rhs) {
            return lhs.length - rhs.length;
        }
    };

    public synchronized byte[] getBuf(int len) {
        for (int i = 0; i < mBuffersBySize.size(); i++) {
            byte[] buf = mBuffersBySize.get(i);
            if (buf.length >= len) {
                mCurrentSize -= buf.length;
                mBuffersBySize.remove(i);
                mBuffersByLastUse.remove(buf);
                return buf;
            }
        }
        return new byte[len];
    }

    public synchronized void returnBuf(byte[] buf) {
        if (buf == null || buf.length > mSizeLimit) {
            return;
        }
        mBuffersByLastUse.add(buf);
        int pos = Collections.binarySearch(mBuffersBySize, buf, BUF_COMPARATOR);
        if (pos < 0) {
            pos = -pos - 1;
        }
        mBuffersBySize.add(pos, buf);
        mCurrentSize += buf.length;
        trim();
    }

    private synchronized void trim() {
        while (mCurrentSize > mSizeLimit) {
            byte[] buf = mBuffersByLastUse.remove(0);
            mBuffersBySize.remove(buf);
            mCurrentSize -= buf.length;
        }
    }
}
