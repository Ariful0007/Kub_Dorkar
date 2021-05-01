package com.example.kub_dorkar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.kub_dorkar.adapter.commentsAdapter;
import com.example.kub_dorkar.model.comments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView mComments;
    private List<comments> mCommentList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private commentsAdapter mAdapter;
    private EditText commentText;
    private ImageButton commentSend;

    private FirebaseFirestore firebaseFirestore;

    private DocumentSnapshot lastVisible;
    private Boolean isFirstPageFirstLoad = true;

    private Toolbar mToolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mToolbar = findViewById(R.id.comment_app_bar);
        mToolbar.setTitle("Comments");
        setSupportActionBar(mToolbar);
        mToolbar.setElevation(10.0f);
        mComments = findViewById(R.id.commentRecyclerView);
        mAdapter = new commentsAdapter(mCommentList);
        mLinearLayout = new LinearLayoutManager(this);
        mComments.setHasFixedSize(true);
        mComments.setLayoutManager(mLinearLayout);
        mComments.setAdapter(mAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        final String blogPostId = getIntent().getStringExtra("blogId");
        final String currentUserId = getIntent().getStringExtra("userid");

        loadMessages(blogPostId);

        mComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Boolean reachedTop = !mComments.canScrollVertically(1);

                if (reachedTop){
                    loadMoreMessages(blogPostId);
                }

            }
        });

        commentText = findViewById(R.id.comment);
        commentSend = findViewById(R.id.comment_send);

        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comment = commentText.getText().toString();
                if (!comment.isEmpty()){
                    commentText.setEnabled(false);

                    commentSend.setEnabled(false);

                    Map<String,Object> commentMap = new HashMap<>();
                    commentMap.put("message",comment);
                    commentMap.put("userId",currentUserId);
                    commentMap.put("timestamp",System.currentTimeMillis());

                    firebaseFirestore.collection("Posts/"+blogPostId+"/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (!task.isSuccessful()){

                                commentText.setEnabled(true);
                                commentSend.setEnabled(true);

                            } else {

                                commentText.setEnabled(true);
                                commentSend.setEnabled(true);

                                commentText.setText("");
                            }
                        }
                    });
                    mComments.scrollToPosition(0);
                }

            }
        });

    }

    private void loadMessages(String mBlogId){
        Query loadMessageQuery = firebaseFirestore.collection("Posts").document(mBlogId)
                .collection("Comments").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        loadMessageQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("BlogAdapterComments", "listen:error", e);
                    return;
                }

                if (!documentSnapshots.isEmpty()) {
                    Log.i("chatActivityCount","Empty");

                    if (isFirstPageFirstLoad){
                        lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
                    }

                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            comments message = doc.getDocument().toObject(comments.class);

                            if (isFirstPageFirstLoad){
                                mCommentList.add(message);
                            } else {
                                mCommentList.add(0,message);
                            }

                            mAdapter.notifyDataSetChanged();
                            mComments.scrollToPosition(0);
                        }
                    }
                    isFirstPageFirstLoad = false;
                }
            }
        });

    }

    private void loadMoreMessages(String mBlogId){
        Query nextQuery = firebaseFirestore.collection("Posts").document(mBlogId)
                .collection("Comments").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastVisible).limit(15);
        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("CHAT_ACTIVITY", "listen:error", e);
                    return;
                }
                if (!documentSnapshots.isEmpty()) {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            comments message = doc.getDocument().toObject(comments.class);
                            mCommentList.add(message);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

}
