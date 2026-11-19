package com.mastercash.mastercash.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextInputType
import androidx.compose.ui.unit.dp
import com.mastercash.mastercash.domain.MovimentacaoDomain
import com.mastercash.mastercash.ui.viewmodel.MovimentacaoViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mastercash.mastercash.utils.FormatUtils

@Composable
fun FormScreen(
    navController: androidx.compose.navigation.NavHostController,
    editId: Int = -1
) {
    val viewModel: MovimentacaoViewModel = hiltViewModel()

    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("entrada") }
    var categoria by remember { mutableStateOf("Alimentação") }
    var data by remember { mutableStateOf("") }
    var novaCategoria by remember { mutableStateOf("") }
    var showingNovaCategoria by remember { mutableStateOf(false) }

    val categorias by viewModel.categorias
    val categoriaList = categorias ?: emptyList()

    LaunchedEffect(editId) {
        if (editId != -1) {
            val movimentacoes = viewModel as MovimentacaoViewModel
            // Note: in a real app, we'd observe the specific item; here we simplify
        }
    }

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
                    contentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = TextInputType.Number,
                    decimalSeparator = ","
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = androidx.compose.foundation.layout.Alignment.CenterVertically
            ) {
                Text(
                    text = "Tipo:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(60.dp)
                )
                SegmentedButton(
                    selectedValues = setOf(tipo),
                    onValueChange = { selection -> tipo = selection.firstOrNull() ?: "entrada" },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    SegmentedButtonOption(
                        value = "entrada",
                        label = { Text(text = "Entrada") }
                    )
                    SegmentedButtonOption(
                        value = "saida",
                        label = { Text(text = "Saída") }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = androidx.compose.foundation.layout.Alignment.CenterVertically
            ) {
                Text(
                    text = "Categoria:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(80.dp)
                )
                DropdownMenu(
                    expanded = showingNovaCategoria,
                    onDismissRequest = { showingNovaCategoria = false }
                ) {
                    categoriaList.forEach { cat ->
                        DropdownMenuItem(
                            onClick = {
                                categoria = cat.nome
                                showingNovaCategoria = false
                            }
                        ) {
                            Text(text = cat.nome)
                        }
                    }
                    Divider()
                    DropdownMenuItem(
                        onClick = {
                            showingNovaCategoria = true
                            novaCategoria = ""
                        }
                    ) {
                        Text(text = "Nova categoria...")
                    }
                }

                TextButton(
                    onClick = { showingNovaCategoria = !showingNovaCategoria }
                ) {
                    Text(
                        text = categoria,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            OutlinedTextField(
                value = data,
                onValueChange = { data = it },
                label = { Text("Data (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = TextInputType.Date
                ),
                singleLine = true
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
                    )
                ) {
                    Text(text = "Cancelar")
                }

                Button(
                    onClick = {
                        try {
                            val v = valor.replace(",", ".").toDouble()
                            viewModel.addMovimentacao(descricao, v, tipo, categoria, data)
                            navController.popBackStack()
                        } catch (e: NumberFormatException) {
                            viewModel.showToast("Valor inválido")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Salvar")
                }
            }

            // Adicionar nova categoria inline
            if (showingNovaCategoria) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    elevation = 2.dp,
                    containerColor = MaterialTheme.colorScheme.surface
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
