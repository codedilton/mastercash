package com.mastercash.mastercash.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mastercash.mastercash.domain.MovimentacaoDomain
import com.mastercash.mastercash.ui.viewmodel.MovimentacaoViewModel
import com.mastercash.mastercash.utils.FormatUtils
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: androidx.navigation.NavHostController, modifier: Modifier = Modifier) {
    val viewModel: MovimentacaoViewModel = hiltViewModel()

    val movimentacoes by viewModel.movimentacoes.observeAsState(emptyList())
    val totalEntradas by viewModel.totalEntradas.observeAsState(0.0)
    val totalSaidas by viewModel.totalSaidas.observeAsState(0.0)
    val saldo by viewModel.saldo.observeAsState(0.0)
    val categoriaMaiorGasto by viewModel.categoriaMaiorGasto.observeAsState("")
    val toastMessage by viewModel.toastMessage.observeAsState("")

    val scope = rememberCoroutineScope()

    LaunchedEffect(toastMessage) {
        if (toastMessage.isNotEmpty()) {
            scope.launch {
                // Snackbar or toast handling
            }
        }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("form") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar movimentação",
                    tint = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "MasterCash", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Dashboard Cards (Side by side)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DashboardCard(
                        title = "Total de Entradas",
                        value = FormatUtils.formatCurrency(totalEntradas ?: 0.0),
                        color = Color(0xFF22C55E),
                        modifier = Modifier.weight(1f)
                    )
                    DashboardCard(
                        title = "Total de Saídas",
                        value = FormatUtils.formatCurrency(totalSaidas ?: 0.0),
                        color = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DashboardCard(
                        title = "Balanço",
                        value = FormatUtils.formatCurrency(saldo ?: 0.0),
                        color = if ((saldo ?: 0.0) >= 0) Color(0xFF16A34A) else Color(0xFFDC2626),
                        modifier = Modifier.weight(1f)
                    )
                    DashboardCard(
                        title = "Maior Gasto",
                        value = if (categoriaMaiorGasto.isNullOrEmpty()) "-" else categoriaMaiorGasto,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtros
            var filtroDescricao by remember { mutableStateOf("") }
            var filtroCategoria by remember { mutableStateOf("") }

            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = filtroDescricao,
                    onValueChange = { filtroDescricao = it },
                    label = { Text("Pesquisar por descrição") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                DropdownMenu(
                    expanded = filtroCategoria.isNotEmpty(),
                    onDismissRequest = { filtroCategoria = "" }
                ) {
                    val categoriasFixas = listOf("Alimentação", "Transporte", "Moradia", "Lazer", "Outros")
                    categoriasFixas.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(text = cat) },
                            onClick = { filtroCategoria = cat }
                        )
                    }
                }

                TextButton(
                    onClick = { filtroCategoria = "" }
                ) {
                    Text(text = "Limpar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de Movimentações
            Text(
                text = "Movimentações Recentes",
                style = MaterialTheme.typography.titleMedium
            )

            val listaFiltrada = if (movimentacoes != null) {
                movimentacoes.filter { m ->
                    (filtroDescricao.isEmpty() || m.descricao.lowercase().contains(filtroDescricao.lowercase())) &&
                        (filtroCategoria.isEmpty() || m.categoria == filtroCategoria)
                }
            } else {
                emptyList()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (listaFiltrada.isEmpty()) {
                    Text(
                        text = "Nenhuma movimentação encontrada.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(listaFiltrada) { m ->
                            MovimentacaoItem(
                                movimentacao = m,
                                onEdit = { navController.navigate("edit/${m.id}") },
                                onDelete = { viewModel.deleteMovimentacao(m.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MovimentacaoItem(
    movimentacao: MovimentacaoDomain,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val tipoCor = if (movimentacao.tipo == "entrada") Color(0xFF22C55E) else Color(0xFFEF4444)

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movimentacao.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${FormatUtils.formatDateToBr(movimentacao.data)}  •  ${movimentacao.categoria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Column {
                Text(
                    text = FormatUtils.formatCurrency(movimentacao.valor),
                    style = MaterialTheme.typography.bodyMedium,
                    color = tipoCor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }
}
