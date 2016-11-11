package theokanning.rover.ui.fragment.robot;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import theokanning.rover.R;
import theokanning.rover.chat.model.Message;
import theokanning.rover.ui.activity.RobotActivity;

/**
 * Fragment displayed on the robot while driving
 *
 * @author Theo Kanning
 */
public class ConnectedFragment extends Fragment implements ChatMessageDebugListener {

    @Bind(R.id.commandList)
    ListView commandList;

    private ArrayAdapter<String> arrayAdapter;

    public static ConnectedFragment newInstance() {
        ConnectedFragment fragment = new ConnectedFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected, container, false);
        ButterKnife.bind(this,view);
        initializeList();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((RobotActivity) context).registerChatMessageDebugListener(this);
    }

    @Override
    public void onDetach() {
        ((RobotActivity) getActivity()).unregisterChatMessageDebugListener();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initializeList(){
        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.row_chat_message, new ArrayList<>());
        commandList.setAdapter(arrayAdapter);
    }

    @OnClick(R.id.clear)
    public void clearList(){
        arrayAdapter.clear();
    }

    @Override
    public void showMessage(Message message) {
        getActivity().runOnUiThread(() -> {
            String time = DateTime.now().toString("hh:mm:ss.SSS");
            arrayAdapter.insert(time + ": " + message.getContents(), 0);
        });
    }
}
