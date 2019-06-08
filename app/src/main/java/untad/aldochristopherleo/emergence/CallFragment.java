package untad.aldochristopherleo.emergence;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CallFragment extends Fragment {

    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
        listView = view.findViewById(R.id.listCall);

        HashMap<String, String> hashMap = new HashMap<>();
        String[] arrCall = getResources().getStringArray(R.array.call);
        final String[] arrCallNo = getResources().getStringArray(R.array.callNo);
        for (int i = 0; i < arrCall.length; i++){
            hashMap.put(arrCall[i], arrCallNo[i]);
        }
        List<HashMap<String, String>> list = new ArrayList<>();
        SimpleAdapter adapter = new SimpleAdapter(getContext(), list, R.layout.listcall,
                new String[]{"First Line", "Second Line"}, new int[]{R.id.callName, R.id.callNo});
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()){
            HashMap<String, String> hashList = new HashMap<>();
            Map.Entry entry = (Map.Entry) iterator.next();
            hashList.put("First Line", entry.getKey().toString());
            hashList.put("Second Line", "No. Telp: "+entry.getValue().toString());
            list.add(hashList);
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ arrCallNo[position]));
                startActivity(intent);

            }
        });
        return view;
    }
}
