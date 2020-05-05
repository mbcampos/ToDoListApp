package com.example.todolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText txtNovaTarefa;
    private Button btnNovaTarefa;
    private SQLiteDatabase bancoDados;
    private ListView ltvTarefas;
    private ArrayList<String> listItens;
    private ArrayList<Integer> listIds;
    private ArrayAdapter<String> listaAdaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNovaTarefa = (EditText)findViewById(R.id.txtNovaTarefa);
        btnNovaTarefa = (Button)findViewById(R.id.btnNovaTarefa);
        ltvTarefas = (ListView)findViewById(R.id.ltvTarefas);

        // Cria a tabela de tarefas
        bancoDados = openOrCreateDatabase("appToDoList", MODE_PRIVATE, null);
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, descricao VARCHAR)");

        recuperarLista();

        btnNovaTarefa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String txtTarefa = txtNovaTarefa.getText().toString();
                    bancoDados.execSQL(String.format("INSERT INTO tarefas (descricao) VALUES ('%s')", txtTarefa));
                    Toast.makeText(MainActivity.this, "Item inserido oom sucesso!", Toast.LENGTH_SHORT).show();
                    recuperarLista();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ltvTarefas.setLongClickable(true);
        ltvTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Integer itemId = listIds.get(position);
                removerTarefa(itemId);
                return true;
            }
        });
    }

    private void recuperarLista() {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas", null);

            listItens = new ArrayList<String>();
            listIds = new ArrayList<Integer>();
            listaAdaptador = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItens);
            ltvTarefas.setAdapter(listaAdaptador);

            // Recupera o id da coluna "descrição"
            int indiceId = cursor.getColumnIndex("id");
            int indiceDescricao = cursor.getColumnIndex("descricao");

            cursor.moveToFirst();
            while (cursor != null) {
                listItens.add(cursor.getString(indiceDescricao));
                listIds.add(cursor.getInt(indiceId));
                cursor.moveToNext();
            }

            // Limpa o campo
            txtNovaTarefa.setText("");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removerTarefa(int id) {
        try {
            bancoDados.execSQL(String.format("DELETE FROM tarefas WHERE id = %d", id));
            Toast.makeText(MainActivity.this, "Item excluído oom sucesso!", Toast.LENGTH_SHORT).show();
            recuperarLista();
        }
        catch (Exception e) {
            Toast.makeText(MainActivity.this, "Houve um erro ao tentar excluir o item", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
