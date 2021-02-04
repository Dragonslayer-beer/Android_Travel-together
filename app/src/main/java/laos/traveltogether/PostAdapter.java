package laos.traveltogether;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context mContext;
    List<Post> mData;
    private FirebaseUser firebaseUser;
    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Post post = mData.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.tvTitle.setText(mData.get(position).getTitle());
        Glide.with(mContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);
        isLike(post.getPostKey(),holder.Like);
        nrLikes(holder.likes,post.getPostKey());
        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.Like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostKey()).child(firebaseUser.getUid()).setValue(true);

                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostKey()).child(firebaseUser.getUid()).removeValue();

                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView imgPost;
        ImageView imgPostProfile;
        ImageView Like;
        TextView likes;
        public MyViewHolder(View itemView) {
            super(itemView);
            Like = itemView.findViewById(R.id.Like);
            likes = itemView.findViewById(R.id.likes);
            tvTitle = itemView.findViewById(R.id.row_post_title);
            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent postDetailActivity = new Intent(mContext, PostDetail_Activity.class);
                    int position = getAdapterPosition();
                    postDetailActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    // will fix this later i forgot to add user name to post object
                    //postDetailActivity.putExtra("userName",mData.get(position).getUsername);
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate", timestamp);
                    mContext.startActivity(postDetailActivity);
                }
            });
        }

    }
    private void isLike(String PostKey, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(PostKey);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_like_24);
                    imageView.setTag("liked");

                }else {
                    imageView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void nrLikes(final TextView likes, String PostKey){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(PostKey);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
