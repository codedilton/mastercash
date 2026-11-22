package com.mastercash.mastercash.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.mastercash.mastercash.domain.MovimentacaoDomain
import com.mastercash.mastercash.ui.viewmodel.MovimentacaoViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.mastercash.mastercash.utils.FormatUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: androidx.navigation.NavHostController,
    editId: Int = -1
) {
    val viewModel: MovimentacaoViewModel = hiltViewModel()

    val dateFormatBr = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val todayBr = dateFormatBr.format(Date())

    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("entrada") }
    var categoria by remember { mutableStateOf("Alimentação") }
    var data by remember { mutableStateOf(todayBr) }
    var novaCategoria by remember { mutableStateOf("") }
    var showingNovaCategoria by remember { mutableStateOf(false) }

    val categorias by viewModel.categorias.observeAsState(emptyList())
    val categoriaList = categorias

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (editId != -1) "Editar Movimentação" else "Nova Movimentação",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Preencha os dados da movimentação",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor (R$)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Tipo:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(60.dp)
                )
                Button(
                    onClick = { tipo = "entrada" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tipo == "entrada") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (tipo == "entrada") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Entrada")
                }
                Button(
                    onClick = { tipo = "saida" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tipo == "saida") Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (tipo == "saida") Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Saída")
                }
            }

            // Dropdown / Categoria selection list
            var categoryExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                OutlinedButton(onClick = { categoryExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Categoria: $categoria")
                }
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categoriaList.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(text = cat.nome) },
                            onClick = {
                                categoria = cat.nome
                                categoryExpanded = false
                            }
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(text = "Nova categoria...") },
                        onClick = {
                            categoryExpanded = false
                            showingNovaCategoria = true
                            novaCategoria = ""
                        }
                    )
                }
            }

            OutlinedTextField(
                value = data,
                onValueChange = { data = FormatUtils.applyDateMask(it) },
                label = { Text("Data (DD/MM/AAAA)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancelar")
                }

                Button(
                    onClick = {
                        try {
                            val v = FormatUtils.parseCurrency(valor)
                            val dataInput = if (data.isBlank()) todayBr else data
                            val dataIso = FormatUtils.parseDateToIso(dataInput)
                            viewModel.addMovimentacao(descricao, v, tipo, categoria, dataIso)
                            navController.popBackStack()
                        } catch (_: NumberFormatException) {
                            viewModel.showToast("Valor inválido")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Salvar")
                }
            }

            // Adicionar nova categoria inline
            if (showingNovaCategoria) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Nova Categoria",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = novaCategoria,
                            onValueChange = { novaCategoria = it },
                            label = { Text("Nome da categoria") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            singleLine = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showingNovaCategoria = false }
                            ) {
                                Text(text = "Cancelar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (novaCategoria.isNotBlank()) {
                                        viewModel.addCategoria(novaCategoria.trim())
                                        categoria = novaCategoria.trim()
                                        showingNovaCategoria = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(text = "Adicionar")
                            }
                        }
                    }
                }
            }
        }
    }
}
