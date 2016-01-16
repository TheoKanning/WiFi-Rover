package theokanning.rover.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import theokanning.rover.R;

/**
 * Shows a message while waiting, message is passed as an argument
 */
public class WaitingFragment extends BaseFragment {

    private static final String TAG = "WaitingFragment";
    public static final String WAITING_TEXT_EXTRA = "WaitingTextExtra";

    @Bind(R.id.waiting_message)
    TextView waitingMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiting, container, false);
        ButterKnife.bind(this, view);

        setMessage();
        return view;
    }

    /**
     * Gets the waiting message from the arguments bundle and displays it
     */
    private void setMessage() {
        Bundle arguments = getArguments();
        String message = arguments.getString(WAITING_TEXT_EXTRA);
        if (message == null) {
            throw new RuntimeException("Waiting argument not given message extra");
        }
        waitingMessage.setText(message);
    }
}
