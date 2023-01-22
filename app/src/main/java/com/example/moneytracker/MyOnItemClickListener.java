package com.example.moneytracker;

//class MyOnItemClickListener implements TransactionAdapter.OnItemClickListener {
//    private final TransactionAdapter adapter;
//    private final Context context;
//
//    MyOnItemClickListener(TransactionAdapter adapter, Context context) {
//        this.adapter = adapter;
//        this.context = context;
//    }
//
//
//
//    @Override
//    public void onItemLongClick(final int position) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Select an option")
//                .setItems(R.array.options, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (which == 0) {
//                            // Edit item
////                            TransactionModel model = adapter.getModelAt(position);
//                            TransactionModel model = adapter.getModelAt(position);
//                            Intent intent = new Intent(context, EditTransactionActivity.class);
//                            intent.putExtra("transactionID", model.getTransactionID());
//                            intent.putExtra("amount", model.getAmount());
//                            intent.putExtra("date", model.getDate());
//                            intent.putExtra("time", model.getTime());
//                            intent.putExtra("category", model.getCategory());
//                            intent.putExtra("note", model.getNote());
//                            intent.putExtra("category", model.getCategory());
//                            intent.putExtra("type", model.getType());
//                            ((Activity) context).finish();
//                            context.startActivity(intent);
//                        } else if (which == 1) {
//                            // Delete item
//                            TransactionModel model = adapter.getModelAt(position);
//                            String transactionID = model.getTransactionID();
//                            deleteTransaction(transactionID);
//                        }
//                    }
//                });
//        builder.create().show();
//    }
//
//
//    private void deleteTransaction(String transactionID) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null){
//            String currentUserUID = user.getUid();
//            String transactionRef = "users/"+currentUserUID.toString()+"/transactions/"+transactionID;
//            DatabaseHandler.getInstance().deleteData(transactionRef);
//        }
//
//
//    }


//}