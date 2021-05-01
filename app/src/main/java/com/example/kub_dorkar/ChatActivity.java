package com.example.kub_dorkar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.kub_dorkar.HelperUtils.GetTimeAgo;
import com.example.kub_dorkar.HelperUtils.GlideLoadImage;
import com.example.kub_dorkar.adapter.MessageAdapter;
import com.example.kub_dorkar.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser, mChatUserImage;
    private String mChatUserName;
    private FirebaseAuth mAuth;


    private TextView mTitleView, mLastSeenView;
    private CircleImageView mProfileImage;

    private String mCurrentUserId;

    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int GALLERY_PICK = 77;

    private static final String SENDING = "sending";
    private static final String SENT = "sent";
    private static final String SEEN = "seen";


    private ImageButton mChatAddBtn;
    private ImageButton mChatSendButton;
    private EditText mChatMessageView;
    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;
    private Bitmap compressedImageFile;
    private Bitmap compressedThumbFile;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private DatabaseReference mUserRef;
    private DatabaseReference mChatRef;

    private LayoutInflater mInflater;
    private View mActionBarView;
    private ActionBar mActionBar;

    private List<String> selectedIds = new ArrayList<>();
    private Map<String,Integer> positionMap = new HashMap<>();
    private boolean isMultiSelect = false;

    private Boolean mCheckSuccess = true;
    private int loopCounter = 0;
    private RecyclerView mMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MainTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setTitle("");
        mChatUser = getIntent().getStringExtra("user_id");
        mChatUserName = getIntent().getStringExtra("name");
        mChatUserImage = getIntent().getStringExtra("image");
        mInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = mInflater.inflate(R.layout.chat_custom_bar,null);
        mActionBar.setCustomView(mActionBarView);
        mActionBar.setElevation(10.0f);

        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendButton = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);

        //firebase
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mLastSeenView = findViewById(R.id.last_seen_custom_bar);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mChatUser);

        mUserRef.child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.getValue().toString();
                if (online.equals("true")){
                    mLastSeenView.setText("Online");
                } else {
                    if (System.currentTimeMillis()-Long.parseLong(online) > 86400000){

                        Date date = new Date(Long.parseLong(online));
                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        mLastSeenView.setText(formatter.format(date));
                    } else {
                        String lastSeenTime = GetTimeAgo.getTimeAgo(Long.parseLong(online),ChatActivity.this);
                        mLastSeenView.setText(lastSeenTime);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        // Custom actionbar items
        mTitleView = (TextView) findViewById(R.id.name_custom_bar);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);
        mTitleView.setText(mChatUserName);
        GlideLoadImage.loadSmallImage(ChatActivity.this,mProfileImage,mChatUserImage,mChatUserImage);

        RequestManager glide = Glide.with(ChatActivity.this);
        mAdapter = new MessageAdapter(messagesList,mChatUserName,glide,ChatActivity.this,mCurrentUserId, mChatUser);
        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mLinearLayout = new LinearLayoutManager(ChatActivity.this);
        mLinearLayout.setReverseLayout(true);
        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);
        mMessagesList.setAdapter(mAdapter);
        //Updating the chat fragment list, as the user with latest chat should come at top
        firebaseFirestore.collection("Chat").document(mCurrentUserId)
                .collection("usernames").document(mChatUser).update("timestamp",System.currentTimeMillis());

        //Marking all messages as seen
        firebaseFirestore.collection("Messages")
                .document(mCurrentUserId).collection(mChatUser)
                .whereEqualTo("seen",false)
                .addSnapshotListener(ChatActivity.this,new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot documentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("ChatActivity", "listen:error", e);
                            return;
                        }
                        for (final DocumentSnapshot doc : documentSnapshots.getDocuments()) {
                            DocumentReference documentReference = doc.getReference();
                            documentReference.update("seen",true);
                        }

                    }
                });

        loadMessages();
    }
    private void loadMessages(){
        Query loadMessageQuery = firebaseFirestore.collection("Messages").document(mCurrentUserId)
                .collection(mChatUser).orderBy("time", Query.Direction.DESCENDING).limit(20);
        loadMessageQuery.addSnapshotListener(ChatActivity.this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("CHAT_ACTIVITY", "listen:error", e);
                    return;
                }
                if (!documentSnapshots.isEmpty()) {

                    if (isFirstPageFirstLoad){
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {

                            String messageId = doc.getDocument().getId();
                            Messages message = doc.getDocument().toObject(Messages.class).withId(messageId);

                            if (isFirstPageFirstLoad){
                                messagesList.add(message);
                                mAdapter.notifyItemInserted(messagesList.size());
                            } else {
                                messagesList.add(0,message);
                                mAdapter.notifyItemInserted(0);
                            }
                            mMessagesList.scrollToPosition(0);
                        }


                    }

                    isFirstPageFirstLoad = false;
                }
            }
        });

    }
}
